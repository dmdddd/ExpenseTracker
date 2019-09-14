//package com.software.dk198.expensetracker.DataBackup;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Environment;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.software.dk198.expensetracker.DBHelper;
//import com.software.dk198.expensetracker.Expenses.Expense;
//import com.software.dk198.expensetracker.MainActivity;
//import com.software.dk198.expensetracker.R;
//import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
//import com.software.dk198.expensetracker.Targets.Target;
//import com.software.dk198.expensetracker.Targets.TargetAdapter;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//
//public class TargetsForImport extends AppCompatActivity {
//
//    final String TAG = "TargetsForImport";
//    public static ArrayList<Target> targets = new ArrayList<Target>();
//    public static TargetForImportAdapter adapter;
//    public static RecyclerView targetsToImportRecyclerView;
//
//    String file;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_targets_for_import);
//
//        // Get extras from the caller
//        Bundle extras = getIntent().getExtras();
//        file = extras.getString("dbFile");
//
//        if(loadTargetsFromDB()){
//            targetsToImportRecyclerView = (RecyclerView) findViewById(R.id.targetsToImportRecyclerView);
//            // Create adapter passing in the targets
//            adapter = new TargetForImportAdapter(targets);
//            // Attach the adapter to the recyclerview to populate items
//            targetsToImportRecyclerView.setAdapter(adapter);
//            // Set layout manager to position the items
//            targetsToImportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        } else {
//            Toast.makeText(this, "Could not load targets from backup", Toast.LENGTH_LONG).show();
//        }
//
//
//
//
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
//                Log.d(TAG, "Loading selected database got us: " + targets.size() + " targets");
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
//}
