//package com.software.dk198.expensetracker.DataBackup;
//
//import android.content.DialogInterface;
//import android.os.Environment;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.SparseBooleanArray;
//import android.view.ActionMode;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.AbsListView;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.software.dk198.expensetracker.DBHelper;
//import com.software.dk198.expensetracker.R;
//import com.software.dk198.expensetracker.Targets.Target;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//
//public class ImportTargetSelection extends AppCompatActivity {
//
//    ListView listView;
//    DBHelper importDatabase;
//    public static ArrayList<Target> targets = new ArrayList<Target>();
//    String[] nameArray;
//    float[] spendingArray;
//    int[] idArray;
//    String file;
//
//    ArrayList<String> nameAL = new ArrayList<>();
//    ArrayList<Float> spendingAL = new ArrayList<>();
//    ArrayList<Integer> idAL = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_import_target_selection);
//
//        // Get extras from the caller
//        Bundle extras = getIntent().getExtras();
//        file = extras.getString("dbFile");
//
//        loadTargetsFromDB();
//
//
//        final CharSequence[] dialogList = nameAL.toArray(new CharSequence[nameAL.size()]);
//        final android.app.AlertDialog.Builder builderDialog = new android.app.AlertDialog.Builder(this);
//        builderDialog.setTitle("Select Item");
//        int count = dialogList.length;
//        boolean[] is_checked = new boolean[count];
//
//        // Creating multiple selection by using setMutliChoiceItem method
//        builderDialog.setMultiChoiceItems(dialogList, is_checked,
//                new DialogInterface.OnMultiChoiceClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int whichButton, boolean isChecked) {
//                    }
//                });
//
//        builderDialog.setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        ListView list = ((android.app.AlertDialog) dialog).getListView();
//                        //ListView has boolean array like {1=true, 3=true}, that shows checked items
//                        Toast.makeText(ImportTargetSelection.this, "Selected: " + list.toString(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//        android.app.AlertDialog alert = builderDialog.create();
//        alert.show();
//
////        final CustomListAdapter listviewAdapter = new CustomListAdapter(this, nameAL.toArray(new String[0]), spendingAL.toArray(new Float[0]), idAL.toArray(new Integer[0]));
////        listView = (ListView) findViewById(R.id.targetListView);
////        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
////        listView.setAdapter(listviewAdapter);
//
////        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
////
////            @Override
////            public void onItemCheckedStateChanged(ActionMode mode,
////                                                  int position, long id, boolean checked) {
////                // Capture total checked items
////                final int checkedCount = listView.getCheckedItemCount();
////                // Set the CAB title according to total checked items
////                mode.setTitle(checkedCount + " Selected");
////                // Calls toggleSelection method from ListViewAdapter Class
////                listviewAdapter.toggleSelection(position);
////            }
////
////            @Override
////            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
////                switch (item.getItemId()) {
////                    case R.id.delete:
////                        // Calls getSelectedIds method from ListViewAdapter Class
////                        SparseBooleanArray selected = listviewAdapter
////                                .getSelectedIds();
////                        // Captures all selected ids with a loop
////                        for (int i = (selected.size() - 1); i >= 0; i--) {
////                            if (selected.valueAt(i)) {
////                                WorldPopulation selecteditem = listviewAdapter
////                                        .getItem(selected.keyAt(i));
////                                // Remove selected items following the ids
//////                                listviewadapter.remove(selecteditem);
////                            }
////                        }
////                        // Close CAB
////                        mode.finish();
////                        return true;
////                    default:
////                        return false;
////                }
////            }
////
////            @Override
////            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
////                mode.getMenuInflater().inflate(R.menu.activity_main, menu);
////                return true;
////            }
////
////            @Override
////            public void onDestroyActionMode(ActionMode mode) {
////                // TODO Auto-generated method stub
////                listviewadapter.removeSelection();
////            }
////
////            @Override
////            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
////                // TODO Auto-generated method stub
////                return false;
////            }
////        });
//
//    }
//
//    boolean loadTargetsFromDB(){
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            // Opening the original database
//            DBHelper database = new DBHelper(this);
//
//            // Copying the selected file to internal storage, where all the DB files are located
//            File db_file_to_import_from = new File(this.getDatabasePath(database.DATABASE_NAME).getParent(), "importDB");
//            try {
//                Log.d("BackupFileAdapter", "Writing allowed");
//                // Opening the backup as input
//                InputStream myInput = new FileInputStream(file);
//
//                // Open the internal storage for copying
//                OutputStream myOutput = new FileOutputStream(db_file_to_import_from);
//
//                // transfer bytes from the input file to the output file
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = myInput.read(buffer)) > 0) {
//                    myOutput.write(buffer, 0, length);
//                }
//                // Close the streams
//                myOutput.flush();
//                myOutput.close();
//                myInput.close();
//
//                // Opening the import database
//                DBHelper import_db = new DBHelper(this, db_file_to_import_from.getName());
//                // Importing all the targets
//                targets =  import_db.getAllTargets();
//                Log.d("ImportTargetSelection", "Loading selected database got us: " + targets.size() + " targets");
//
//                for (Target target : targets){
//                    nameAL.add(target.getName());
//                    spendingAL.add(target.getTotalSpendings());
//                    idAL.add(target.getId());
//                }
//
//                return true;
////                int target_counter = 0;
////                for (Target target : targets) {
////                    // Adding each target
////                    Target added_tartget = database.insertTarget(target);
////                    // Getting and adding it's spending categories
////                    ArrayList<SpendingCategory> categories_for_target = import_db.getDifferentCategoriesOfTarget(target);
////                    for (SpendingCategory category: categories_for_target) {
////                        SpendingCategory added_category = database.addSpendingCategoryToTarget_by_id(added_tartget.getId(), category);
////
////                        ArrayList<Expense> expenses_of_target_in_category = import_db.getExpensesByTargetIdAndCategoryId(target.getId(), category.getCategory_id());
////                        for (Expense expense: expenses_of_target_in_category) {
////                            database.insertExpense(added_tartget.getId(), added_category.getCategory_id(), expense.getAmount(), expense.getDate(), expense.getDetails());
////                        }
////                    }
////                }
////                Toast.makeText(this, "Databse updated", Toast.LENGTH_LONG).show();
////
////                ((Activity)viewHolder.context).finish();
////                Intent intent = new Intent(this, MainActivity.class);
////                this.startActivity(intent);
//            }
//            catch (IOException e) {
//                return false;
////                Log.e(TAG, "File write failed: " + e.toString());
//            }
//        }
//
//        // If all worked out
//        return false;
//    }
//
//}
