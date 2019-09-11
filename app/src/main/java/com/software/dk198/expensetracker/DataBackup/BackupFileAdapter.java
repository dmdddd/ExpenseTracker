package com.software.dk198.expensetracker.DataBackup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.Expenses.Expense;
import com.software.dk198.expensetracker.MainActivity;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.Target;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BackupFileAdapter extends RecyclerView.Adapter<BackupFileAdapter.ViewHolder> {
    private static final String TAG = "TargetAdapter";
    private ArrayList<String> files;


    public BackupFileAdapter(ArrayList<String> files) {
        this.files = files;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        private TextView fileNameTxtView;
        private ImageButton removeBtn;
        private Button importBtn;
        private Context context;
        private ArrayList<Target> targets;
        ArrayList<String> targetNames = new ArrayList<>();
        DBHelper database;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder (Context context, View fileView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(fileView);

            database = new DBHelper(context);
            fileNameTxtView = (TextView) itemView.findViewById(R.id.targetName);
            importBtn = (Button) itemView.findViewById(R.id.importBackupBtn);
            removeBtn = (ImageButton) itemView.findViewById(R.id.removeFileBtn);
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {

            Log.d(TAG, "Backup item clicked: " + fileNameTxtView.getText());

            int position = getAdapterPosition(); // gets item position
            final DBHelper importDB = loadTargetsFromDB(this, files.get(position));

            final CharSequence[] dialogList = targetNames.toArray(new CharSequence[targetNames.size()]);
            final android.app.AlertDialog.Builder builderDialog = new android.app.AlertDialog.Builder(context);
            builderDialog.setTitle("Select Item");
            int count = dialogList.length;
            final boolean[] is_checked = new boolean[count];

            // Creating multiple selection by using setMutliChoiceItem method
            builderDialog.setMultiChoiceItems(dialogList, is_checked,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton, boolean isChecked) {
                            // Select items as (un)checked
                            is_checked[whichButton] = isChecked;
                        }
                    });
            final ViewHolder vh = (ViewHolder) view.getTag();
            builderDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<Integer> selectedItems = new ArrayList<>();


                            for (int i = 0; i < is_checked.length; i++)
                                if (is_checked[i]) {
                                    selectedItems.add(targets.get(i).getId());
                                }

                            Log.d(TAG, "Selected targets for import: " + selectedItems);


                            for (Integer selected : selectedItems) {
                                Target target = targets.get(selected);
                                Log.d(TAG, "Got target: " + target.getName());

                                // Adding each target
                                Target added_tartget = database.insertTarget(target);
                                // Getting and adding it's spending categories
                                ArrayList<SpendingCategory> categories_for_target = importDB.getDifferentCategoriesOfTarget(target);
                                for (SpendingCategory category : categories_for_target) {
                                    SpendingCategory added_category = database.addSpendingCategoryToTarget_by_id(added_tartget.getId(), category);

                                    ArrayList<Expense> expenses_of_target_in_category = importDB.getExpensesByTargetIdAndCategoryId(target.getId(), category.getCategory_id());
                                    for (Expense expense : expenses_of_target_in_category) {
                                        database.insertExpense(added_tartget.getId(), added_category.getCategory_id(), expense.getAmount(), expense.getDate(), expense.getDetails());
                                    }
                                }
                            }


                            // TODO - restart main actovity and show success Toast
                            ((Activity)vh.context).finish();
                            Intent intent = new Intent(vh.context, MainActivity.class);
                            vh.context.startActivity(intent);
//                            Toast.makeText(viewHolder.context, "Databse updated", Toast.LENGTH_LONG).show();
                        }
                    });

            android.app.AlertDialog alert = builderDialog.create();
            alert.show();
        }


        DBHelper loadTargetsFromDB(ViewHolder viewHolder, String file){
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // Opening the original database
                DBHelper database = new DBHelper(viewHolder.context);

                // Copying the selected file to internal storage, where all the DB files are located
                File db_file_to_import_from = new File(viewHolder.context.getDatabasePath(database.DATABASE_NAME).getParent(), "importDB");
                try {
                    Log.d("BackupFileAdapter", "Writing allowed");
                    // Opening the backup as input
                    InputStream myInput = new FileInputStream(file);

                    // Open the internal storage for copying
                    OutputStream myOutput = new FileOutputStream(db_file_to_import_from);

                    // transfer bytes from the input file to the output file
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = myInput.read(buffer)) > 0) {
                        myOutput.write(buffer, 0, length);
                    }
                    // Close the streams
                    myOutput.flush();
                    myOutput.close();
                    myInput.close();

                    // Opening the import database
                    DBHelper import_db = new DBHelper(viewHolder.context, db_file_to_import_from.getName());
                    // Importing all the targets
                    targets =  import_db.getAllTargets();
                    Log.d("ImportTargetSelection", "Loading selected database got us: " + targets.size() + " targets");

                    targetNames.clear();
                    for (Target target : targets){
                        targetNames.add(target.getName());
                    }
                    return import_db;
                }
                catch (IOException e) {
                    return null;
                }
            }

            return null;
        }

    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public BackupFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
//        View targetView = inflater.inflate(R.layout.item_target, parent, false);
        View fileView = inflater.inflate(R.layout.item_file_cardview, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(context, fileView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final BackupFileAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final String file = files.get(position);
        final File f = new File(file);
        System.out.println(f.getName());
        // Set item views based on your views and data model
        final TextView textView = viewHolder.fileNameTxtView;
        textView.setText(f.getName());

        viewHolder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                if (f.delete()){
                                    Toast.makeText(viewHolder.context, f.getName() + " has been deleted", Toast.LENGTH_SHORT).show();
                                    int index = 0;
                                    for (String file_path : ChoosingBackupFileActivity.file_paths){
                                        if (file_path.equals(file)){
                                            break;
                                        }
                                        index++;
                                    }
                                    ChoosingBackupFileActivity.file_paths.remove(index);
                                    ChoosingBackupFileActivity.adapter.notifyItemRemoved(index);
                                } else {
                                    Toast.makeText(viewHolder.context, "Could not delete " + f.getName(), Toast.LENGTH_SHORT).show();
                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.removeBtn.getContext());
                builder.setMessage("Are you sure you want to remove this backup file?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        viewHolder.importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("BackupFileAdapter", "Backup file adapter clicked - import");
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String state = Environment.getExternalStorageState();
                                if (Environment.MEDIA_MOUNTED.equals(state)) {
                                    // Opening the original backup database
                                    DBHelper database = new DBHelper(viewHolder.context);

                                    // Copying the selected file to internal storage, where all the DB files are located
                                    File db_file_to_import_from = new File(viewHolder.context.getDatabasePath(database.DATABASE_NAME).getParent(), "importDB.db");
                                    try {
                                        Log.d("BackupFileAdapter", "Writing allowed");
                                        // Opening the backup as input
                                        InputStream myInput = new FileInputStream(file);

                                        // Open the internal storage for copying
                                        OutputStream myOutput = new FileOutputStream(db_file_to_import_from);

                                        // transfer bytes from the input file to the output file
                                        byte[] buffer = new byte[1024];
                                        int length;
                                        while ((length = myInput.read(buffer)) > 0) {
                                            myOutput.write(buffer, 0, length);
                                        }
                                        // Close the streams
                                        myOutput.flush();
                                        myOutput.close();
                                        myInput.close();

                                        // Opening the import database
                                        DBHelper import_db = new DBHelper(viewHolder.context, db_file_to_import_from.getName());
                                        // Importing all the targets
                                        import_db.checkpoint();
                                        ArrayList<Target> import_targets =  import_db.getAllTargets();
                                        int target_counter = 0;
                                        for (Target target: import_targets) {

                                            Log.d(TAG, "Got target: " + target.getName());

                                            // Adding each target
                                            Target added_tartget = database.insertTarget(target);
                                            // Getting and adding it's spending categories
                                            ArrayList<SpendingCategory> categories_for_target = import_db.getDifferentCategoriesOfTarget(target);
                                            for (SpendingCategory category: categories_for_target) {
                                                SpendingCategory added_category = database.addSpendingCategoryToTarget_by_id(added_tartget.getId(), category);

                                                ArrayList<Expense> expenses_of_target_in_category = import_db.getExpensesByTargetIdAndCategoryId(target.getId(), category.getCategory_id());
                                                for (Expense expense: expenses_of_target_in_category) {
                                                    database.insertExpense(added_tartget.getId(), added_category.getCategory_id(), expense.getAmount(), expense.getDate(), expense.getDetails());
                                                }
                                            }
                                        }
                                        Toast.makeText(viewHolder.context, "Databse updated", Toast.LENGTH_LONG).show();

                                        ((Activity)viewHolder.context).finish();
                                        Intent intent = new Intent(viewHolder.context, MainActivity.class);
                                        viewHolder.context.startActivity(intent);
                                    }
                                    catch (IOException e) {
                                        Log.e("Exception", "File write failed: " + e.toString());
                                    }
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.removeBtn.getContext());
                builder.setMessage("Are you sure you want to import this backup file?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });



    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return files.size();
    }

}
