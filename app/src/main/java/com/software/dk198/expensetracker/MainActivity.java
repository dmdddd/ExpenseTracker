package com.software.dk198.expensetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.software.dk198.expensetracker.Expenses.Expense;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.AddTargetActivity;
import com.software.dk198.expensetracker.Targets.Target;
import com.software.dk198.expensetracker.Targets.TargetAdapter;
import com.software.dk198.expensetracker.DataBackup.BackupActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    DBHelper database;
    public static String language = "en";
    public static ArrayList<Target> targets;
    public static TargetAdapter adapter;
    public static RecyclerView targetsView;
    static Button addTargetButton;
    static TextView chooseTargetTextView;
    public static TextView noSubjectsTextView;

    public static String[] currencies = new String[]{"₪", "$", "€", "hrs.", "min.", "None"};
    public static String[] pie_chart_colors = new String[]{"Vordiplom", "Joyful", "Colorful", "Liberty", "Pastel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Languages
        database = new DBHelper(this);
        if (database.settingsDatabaseEmpty()){  // Check whether the settings entry has already been created
            Log.d(TAG, "Creating new settings entry for: " + MainActivity.language);
            database.createSettingsEntry(MainActivity.language);
            // Creating examples for the first running of the app
            createExamples(database);
        }
        String language = database.getLanguage();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= 17) { config.setLocale(locale); } else { config.locale = locale; }
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_main);

        targets = new ArrayList<Target>();
        chooseTargetTextView = (TextView) findViewById(R.id.textViewTop);
        noSubjectsTextView = (TextView) findViewById(R.id.noSubjectsTextView);
        addTargetButton = (Button) findViewById(R.id.btnAddTarget);
        addTargetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked add Target Button");
                Intent intent = new Intent(MainActivity.this, AddTargetActivity.class);
                startActivity(intent);
            }
        });
        targetsView = (RecyclerView) findViewById(R.id.targetRecyclerView);
        targets = database.getAllTargets();
        // Create adapter passing in the sample user data
        adapter = new TargetAdapter(targets);
        // Attach the adapter to the recyclerview to populate items
        targetsView.setAdapter(adapter);
        // Set layout manager to position the items
        targetsView.setLayoutManager(new LinearLayoutManager(this));

        if (targets.size()>0){
            noSubjectsTextView.setVisibility(View.INVISIBLE);
        } else {
            noSubjectsTextView.setVisibility(View.VISIBLE);
        }

    }

    // Handling the top right corner menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        switch(item_id){
            case R.id.language:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle(getString(R.string._choose_language));
                final String[] languages = { "\uD83C\uDDFA\uD83C\uDDF8 English", "\uD83C\uDDF7\uD83C\uDDFA Russian"};
                mBuilder.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected_lang = languages[i];
                        switch (selected_lang){
                            case "\uD83C\uDDFA\uD83C\uDDF8 English":
                                MainActivity.language = "en";
                                database.changeLanguage("en");
                                // restarting the activity for the language changes to take place
                                finish();
                                startActivity(getIntent());
                                break;
                            case "\uD83C\uDDF7\uD83C\uDDFA Russian":
                                MainActivity.language = "ru";
                                database.changeLanguage("ru");
                                // restarting the activity for the language changes to take place
                                finish();
                                startActivity(getIntent());
                                break;
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

                break;
            case R.id.backup:
                Intent intent = new Intent(MainActivity.this, BackupActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void createExamples(DBHelper database){
        Target target;
        SpendingCategory category;
        //Bills:
        target = database.insertTarget("Bills\uD83D\uDCB8", "€");
        category = database.addSpendingCategoryToTarget(target, "Electricity");
        Expense expense = database.insertExpense(target.getId(), category.getCategory_id(), 150, "", "First electricity bill");
        update_data(expense, category.getCategory_id(), target.getId());
        category = database.addSpendingCategoryToTarget(target, "Gas");
        expense = database.insertExpense(target.getId(), category.getCategory_id(), 120, "", "");
        update_data(expense, category.getCategory_id(), target.getId());
        expense = database.insertExpense(target.getId(), category.getCategory_id(), 74, "", "");
        update_data(expense, category.getCategory_id(), target.getId());
        category = database.addSpendingCategoryToTarget(target, "Internet");
        expense = database.insertExpense(target.getId(), category.getCategory_id(), 50, "", "");
        update_data(expense, category.getCategory_id(), target.getId());
        category = database.addSpendingCategoryToTarget(target, "Water");
        expense = database.insertExpense(target.getId(), category.getCategory_id(), 50, "", "");
        update_data(expense, category.getCategory_id(), target.getId());
        //Trip to Canada:
        target = database.insertTarget("Trip to Canada\uD83C\uDF41", "$");
        category = database.addSpendingCategoryToTarget(target, "Shopping");
        expense = database.insertExpense(target.getId(), category.getCategory_id(), 300, "", "2x Shirt\n1x Sweater");
        update_data(expense, category.getCategory_id(), target.getId());
        category = database.addSpendingCategoryToTarget(target, "Accommodation");
        expense = database.insertExpense(target.getId(), category.getCategory_id(), 40, "", "First night's stay");
        update_data(expense, category.getCategory_id(), target.getId());
        expense = database.insertExpense(target.getId(), category.getCategory_id(), 43, "", "Second night");
        update_data(expense, category.getCategory_id(), target.getId());
        category = database.addSpendingCategoryToTarget(target, "Winter Gear");
        category = database.addSpendingCategoryToTarget(target, "Food");
        expense = database.insertExpense(target.getId(), category.getCategory_id(),70, "", "Grocery Shopping");
        update_data(expense, category.getCategory_id(), target.getId());
    }


    void update_data(Expense expense, int category_id, int target_id){
        // Updating the total spent in category
        SpendingCategory category_to_replace = database.getSpendingCategoryById(category_id);
        float old_total_for_category = database.getSpendingCategoryById(category_id).getSpent_in_category();
        float new_total_for_category = old_total_for_category + Float.valueOf(expense.getAmount().toString());
        database.updateTotalSpentInCategory(category_id, new_total_for_category);

        // Updating the total spent for target(including the textView)
        Target target = database.getTargetById(target_id);
        float old_total_for_target = target.getTotalSpendings();
        float new_total_for_target = old_total_for_target + Float.valueOf(expense.getAmount().toString());
        database.updateTotalSpentForTarget(target_id, new_total_for_target);
//        String currency_symbol = target.getDefault_currency();
//        CategoriesShowingFragment.totalSpent.setText(getString(R.string.total) + " " + String.format("%,.2f", new_total_for_target)+currency_symbol);
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            Uri currFileURI = data.getData();
            String path=currFileURI.getPath();
            Log.d("MainActivity", data.getData().toString());
            Log.d("MainActivity", data.toString());
            Log.d("MainActivity", data.getData().getPath().toString());
            //Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();


            String result;
            Cursor cursor = getContentResolver().query(currFileURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = currFileURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }



            //Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            try {
            File src = new File(path);
            File dst = new File("/data/data/com.software.dk198.expensetracker/databases/", src.getName());
                copyFile(src, dst);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }}


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
