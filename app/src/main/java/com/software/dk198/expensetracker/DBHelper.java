package com.software.dk198.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.software.dk198.expensetracker.Expenses.Expense;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DBHelper extends SQLiteOpenHelper {
    // ---------------------------------- Database initialization ----------------------------------
    private static final String TAG = "DBHelper";
    public static final String DATABASE_NAME = "SpendingDB.db";
    // Columns of the Target table
    public static final String TARGETS_TABLE = "targets";
    public static final String COLUMN_TARGET_ID = "target_id";
    public static final String COLUMN_TARGET_NAME = "target_name";
    public static final String COLUMN_TARGET_TOTAL_SPENT = "target_spent";
    public static final String COLUMN_TARGET_DEFAULT_CURRENCY = "target_currency";
    public static final String COLUMN_TARGET_CHART_COLOR = "target_chart_color";
    // Columns of the Expenses table
    public static final String EXPENSES_TABLE = "expenses";
    public static final String COLUMN_EXPENSE_ID = "id";
    //    public static final String COLUMN_TARGET_ID = "target_id";
    //    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_EXPENSE_AMOUNT = "amount";
    public static final String COLUMN_EXPENSE_DATE = "date";
    public static final String COLUMN_EXPENSE_DETAILS = "details";
    // Columns of the Categories table
    public static final String CATEGORIES_TABLE = "categories";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    //    public static final String COLUMN_TARGET_ID = "target_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_SPENT_IN_CATEGORY = "category_spent";
    // Columns of the settings table
    public static final String SETTINGS_TABLE = "settings";
    public static final String COLUMN_SETTINGS_ID = "settings_id"; // Useless, will stay 1
    public static final String COLUMN_LANGUAGE = "language";

    private String[] targetAllColumns = {COLUMN_TARGET_ID, COLUMN_TARGET_NAME, COLUMN_TARGET_TOTAL_SPENT, COLUMN_TARGET_DEFAULT_CURRENCY, COLUMN_TARGET_CHART_COLOR};
    private String[] expensesAllColumns = {COLUMN_EXPENSE_ID, COLUMN_TARGET_ID, COLUMN_CATEGORY_ID, COLUMN_EXPENSE_AMOUNT, COLUMN_EXPENSE_DATE, COLUMN_EXPENSE_DETAILS};
    private String[] categoriesAllColumns = {COLUMN_CATEGORY_ID, COLUMN_TARGET_ID, COLUMN_CATEGORY_NAME, COLUMN_SPENT_IN_CATEGORY};
    private String[] settingsAllColumns = {COLUMN_SETTINGS_ID, COLUMN_LANGUAGE};

    // not null means, it's a must
    private static final String SQL_CREATE_TABLE_TARGETS = "CREATE TABLE " + TARGETS_TABLE + " ("
            + COLUMN_TARGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TARGET_NAME + " TEXT NOT NULL, "
            + COLUMN_TARGET_TOTAL_SPENT + " REAL NOT NULL, "
            + COLUMN_TARGET_DEFAULT_CURRENCY + " TEXT, "
            + COLUMN_TARGET_CHART_COLOR + " TEXT NOT NULL"
            + ");";

    private static final String SQL_CREATE_TABLE_EXPENSES = "CREATE TABLE " + EXPENSES_TABLE + " ("
            + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TARGET_ID + " INTEGER NOT NULL, "
            + COLUMN_CATEGORY_ID + " INTEGER NOT NULL, "
            + COLUMN_EXPENSE_AMOUNT + " REAL NOT NULL, "
            + COLUMN_EXPENSE_DATE + " TEXT NOT NULL, "
            + COLUMN_EXPENSE_DETAILS + " TEXT"
            + ");";

    private static final String SQL_CREATE_TABLE_EXPENSE_CATEGORIES = "CREATE TABLE " + CATEGORIES_TABLE + " ("
            + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TARGET_ID + " INTEGER NOT NULL, "
            + COLUMN_CATEGORY_NAME + " TEXT NOT NULL, "
            + COLUMN_SPENT_IN_CATEGORY + " REAL NOT NULL"
            + ");";

    private static final String SQL_CREATE_TABLE_SETTINGS = "CREATE TABLE " + SETTINGS_TABLE + " ("
            + COLUMN_SETTINGS_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + COLUMN_LANGUAGE + " TEXT"
            + ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }
    public DBHelper(Context context, String db_name) { super(context, db_name , null, 1); }
    public DBHelper(Context context, int version) {
        super(context, DATABASE_NAME , null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating tables");
        db.execSQL(SQL_CREATE_TABLE_TARGETS);
        db.execSQL(SQL_CREATE_TABLE_EXPENSES);
        db.execSQL(SQL_CREATE_TABLE_EXPENSE_CATEGORIES);
        db.execSQL(SQL_CREATE_TABLE_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // When upgrading from ine DB version to another, BAD - user looses all info
        // Clear all data
        Log.d(TAG, "Upgrading tables(onUpgrade)");
        db.execSQL("DROP TABLE IF EXISTS " + TARGETS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EXPENSES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SETTINGS_TABLE);
        // Recreate tables
        onCreate(db);
    }

    public void clearDatabase(String table_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(db);
    }

    public String[] getColumnNames (String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result =  db.query( table_name, null,  null, null, null, null, null);
        String[] columnNames = result.getColumnNames();
        return columnNames;
    }

    // Needed to get the database ready for import
    public void checkpoint(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("pragma wal_checkpoint;", null);
        res.moveToFirst();
    }

// ------------------------------- Helpful functions - TARGET ----------------------------------

    public Target insertTarget (String target_name, String default_currency) {
        Log.d(TAG, "inserting target: " + target_name);
        Target target = new Target();   // To get the default pie chart color, which is stated in the target class
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_TARGET_NAME, target_name);
        contentValues.put(COLUMN_TARGET_TOTAL_SPENT, 0);
        contentValues.put(COLUMN_TARGET_DEFAULT_CURRENCY, default_currency);
        contentValues.put(COLUMN_TARGET_CHART_COLOR, target.getPieChartColor());

        // Insert the data to the table
        int insertID = (int)(db.insert(TARGETS_TABLE, null, contentValues));
        Cursor result =  db.query( TARGETS_TABLE, targetAllColumns, COLUMN_TARGET_ID + " = " + insertID, null, null, null, null);
        result.moveToFirst();
        Target newTarget = cursorToTarget(result);
        result.close();

        return newTarget;
    }

    // Used during database import
    public Target insertTarget(Target taget_to_insert) {
        Target target = new Target();   // To get the default pie chart color, which is stated in the target class
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_TARGET_NAME, taget_to_insert.getName());
        contentValues.put(COLUMN_TARGET_TOTAL_SPENT, taget_to_insert.getTotalSpendings());
        contentValues.put(COLUMN_TARGET_DEFAULT_CURRENCY, taget_to_insert.getDefault_currency());
        contentValues.put(COLUMN_TARGET_CHART_COLOR, target.getPieChartColor());

        // Insert the data to the table
        int insertID = (int)(db.insert(TARGETS_TABLE, null, contentValues));
        Cursor result =  db.query( TARGETS_TABLE, targetAllColumns, COLUMN_TARGET_ID + " = " + insertID, null, null, null, null);
        result.moveToFirst();
        Target newTarget = cursorToTarget(result);
        result.close();

        return newTarget;
    }

    public void updateTarget(Target target){
        Log.d(TAG, "Updating target: "+target.getName());
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_TARGET_ID, target.getId());
        contentValues.put(COLUMN_TARGET_NAME, target.getName());
        contentValues.put(COLUMN_TARGET_TOTAL_SPENT, target.getTotalSpendings());
        contentValues.put(COLUMN_TARGET_DEFAULT_CURRENCY, target.getDefault_currency());
        contentValues.put(COLUMN_TARGET_CHART_COLOR, target.getPieChartColor());
        db.update(TARGETS_TABLE, contentValues, COLUMN_TARGET_ID+" = "+target.getId(), null);
    }

    public void deleteTargetGivenTarget (Target target) {
        Log.d(TAG, "Deleting target: "+target.getName());
        SQLiteDatabase db = this.getWritableDatabase();
        // Deleting all the payments for this target
        ArrayList<Expense> payments_of_target = getExpensesOfTarget(target);
        if (payments_of_target!= null && !payments_of_target.isEmpty()){
            for (Expense expense : payments_of_target){
                deleteExpense(expense);
            }
        }
        // Deleting different payment categories of a given target
        db.delete(CATEGORIES_TABLE,
                COLUMN_TARGET_ID + " = "  + target.getId(),
                null);
        // Deleting the target
        db.delete(TARGETS_TABLE,
                COLUMN_TARGET_ID + " = "  + target.getId(),
                null);
    }

    // DOESN'T add the expenses to the target
    public Target getTargetById(int target_id){
        Log.d(TAG, "Getting target by id : "+target_id);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TARGETS_TABLE +" WHERE "+COLUMN_TARGET_ID+"="+target_id, null );
        res.moveToFirst();
        Target new_target = cursorToTarget(res);
        res.close();
        return new_target;
    }

    public ArrayList<Target> getAllTargets() {
        Log.d(TAG, "Getting all targets");
        ArrayList<Target> target_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.rawQuery( "select * from " + TARGETS_TABLE + " ORDER BY " + COLUMN_TARGET_ID + " DESC", null );
        if (result != null){
            result.moveToFirst();
            while(!result.isAfterLast()){
                Target target = cursorToTarget(result);
                target_list.add(target);
                result.moveToNext();
            }
        }
        result.close();
        Log.d(TAG, "Targets loaded: " + target_list.size());
        return target_list;
    }

    protected Target cursorToTarget(Cursor cursor){
        Target target = new Target();
        target.setId(cursor.getInt(0));
        target.setName(cursor.getString(1));
        target.setTotalSpent(cursor.getFloat(2));
        target.setDefault_currency(cursor.getString(3));
        target.setPieChartColor(cursor.getString(4));
        target.setDifferentSpendingCategories(getDifferentCategoriesOfTarget(target));
        Log.d(TAG, "Finished translating target: " + target.getName());

        return target;
    }
    public void updateTotalSpentForTarget(int target_id, float amount_spent){
        Log.d(TAG, "Updating total spent for a target: " + target_id + ", spent: " + amount_spent);
        SQLiteDatabase db = this.getReadableDatabase();
        Target target = getTargetById(target_id);

        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_TARGET_ID, target.getId());
        contentValues.put(COLUMN_TARGET_NAME, target.getName());
        contentValues.put(COLUMN_TARGET_TOTAL_SPENT, amount_spent);
        contentValues.put(COLUMN_TARGET_DEFAULT_CURRENCY, target.getDefault_currency());
        contentValues.put(COLUMN_TARGET_CHART_COLOR, target.getPieChartColor());
        db.update(TARGETS_TABLE, contentValues, COLUMN_TARGET_ID+" = "+target_id, null);
    }

    // ------------------------------- Helpful functions - EXPENSE ----------------------------------
    //Given target, category, amount and details
    public  Expense getExpenseById(int expense_id){
        Log.d(TAG, "Getting expense by id: " + expense_id);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + EXPENSES_TABLE +" WHERE "+COLUMN_EXPENSE_ID+"="+expense_id, null );
        res.moveToFirst();
        Expense expense = cursorToExpense(res);
        res.close();
        return expense;
    }

    public Expense insertExpense(int target_id, int category_id, float amount, String date, String details){
        Log.d(TAG, "Adding new expense of: " + amount + ", to target id: "+target_id + ", to category: " + category_id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_TARGET_ID, target_id);
        contentValues.put(COLUMN_CATEGORY_ID, category_id);
        contentValues.put(COLUMN_EXPENSE_AMOUNT, amount);
        if (date.length()>0)    // If date was given, use it
            contentValues.put(COLUMN_EXPENSE_DATE, date);
        else                    // Otherwise supply date
            contentValues.put(COLUMN_EXPENSE_DATE, new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime()));
        contentValues.put(COLUMN_EXPENSE_DETAILS, details);

        // Insert the data to the table
        int insertID = (int)(db.insert(EXPENSES_TABLE, null, contentValues));
        Cursor result =  db.query( EXPENSES_TABLE, expensesAllColumns, COLUMN_EXPENSE_ID + " = " + insertID, null, null, null, null);
        result.moveToFirst();
        Expense new_expense = cursorToExpense(result);
        result.close();

        return new_expense;
    }

    public void updateExpense(Expense expense){
        Log.d(TAG, "updating expense id: " + expense.getExpense_id() + ", in category: " + expense.getCategory_id());
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_EXPENSE_ID, expense.getExpense_id());
        contentValues.put(COLUMN_TARGET_ID, expense.getTarget_id());
        contentValues.put(COLUMN_CATEGORY_ID, expense.getCategory_id());
        contentValues.put(COLUMN_EXPENSE_AMOUNT, expense.getAmount());
        contentValues.put(COLUMN_EXPENSE_DATE, expense.getDate());
        contentValues.put(COLUMN_EXPENSE_DETAILS, expense.getDetails());

        // Insert the data to the table
        db.update(EXPENSES_TABLE, contentValues, COLUMN_EXPENSE_ID+" = "+expense.getExpense_id(), null);
    }

    protected Expense cursorToExpense(Cursor cursor){
        Expense expense = new Expense();
        expense.setExpense_id(cursor.getInt(0));
        expense.setTarget_id(cursor.getInt(1));
        expense.setCategory_id(cursor.getInt(2));
        expense.setAmount(cursor.getFloat(3));
        expense.setDate(cursor.getString(4));
        expense.setDetails(cursor.getString(5));
        return expense;
    }

    public ArrayList<Expense> getExpensesOfTarget(Target target) {
        Log.d(TAG, "Getting all the expenses for: " + target.getName());
        ArrayList<Expense> expenses_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.query( EXPENSES_TABLE, expensesAllColumns, COLUMN_TARGET_ID + " = " + target.getId(), null, null, null, COLUMN_EXPENSE_ID+" DESC");
        if (result != null){
            result.moveToFirst();
            while(!result.isAfterLast()){
                Expense expense = cursorToExpense(result);
                expenses_list.add(expense);
                result.moveToNext();
            }
        }
        result.close();
        return expenses_list;
    }

    public ArrayList<Expense> getExpensesByTargetIdAndCategoryId(int target_id, int category_id) {
        Log.d(TAG, "Getting all the expenses for target: " + target_id + ", category: " + category_id);
        ArrayList<Expense> expenses_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.query( EXPENSES_TABLE, expensesAllColumns, COLUMN_TARGET_ID + " = " + target_id+" AND " + COLUMN_CATEGORY_ID + " = " + category_id, null, null, null, COLUMN_EXPENSE_ID+" DESC");
        if (result != null){
            result.moveToFirst();
            while(!result.isAfterLast()){
                Expense expense = cursorToExpense(result);
                expenses_list.add(expense);
                result.moveToNext();
            }
        }
        result.close();
        return expenses_list;
    }

    public boolean deleteExpense(Expense expense_to_delete){
        SQLiteDatabase db = this.getWritableDatabase();
        // Deleting all the expenses in current category
        db.delete(EXPENSES_TABLE, COLUMN_EXPENSE_ID + " = "  + expense_to_delete.getExpense_id(), null);
        return true;
    }

    // ------------------------------- Helpful functions - CATEGORIES ----------------------------------
    public ArrayList<SpendingCategory> getDifferentCategoriesOfTarget(Target target){
        ArrayList<SpendingCategory> categories = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.query( CATEGORIES_TABLE, categoriesAllColumns, COLUMN_TARGET_ID + " = " + target.getId(), null, null, null, COLUMN_CATEGORY_ID+" DESC");
        if (result != null){
            result.moveToFirst();
            while(!result.isAfterLast()){
                SpendingCategory category = cursorToSpendingCategory(result);
                categories.add(category);
                result.moveToNext();
            }
        }
        return categories;
    }

    public SpendingCategory getSpendingCategoryById(int category_id){
        SpendingCategory category;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.query( CATEGORIES_TABLE, categoriesAllColumns, COLUMN_CATEGORY_ID + " = " + category_id, null, null, null, null);
        result.moveToFirst();

        category = cursorToSpendingCategory(result);
        result.close();

        return category;

    }

    public SpendingCategory addSpendingCategoryToTarget(Target target, String category_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_TARGET_ID, target.getId());
        contentValues.put(COLUMN_CATEGORY_NAME, category_name);
        contentValues.put(COLUMN_SPENT_IN_CATEGORY, 0);
        // Insert the data to the table
        int insertID = (int) (db.insert(CATEGORIES_TABLE, null, contentValues));
        Cursor result = db.query(CATEGORIES_TABLE, categoriesAllColumns, COLUMN_CATEGORY_ID + " = " + insertID, null, null, null, null);
        result.moveToFirst();
        SpendingCategory category = cursorToSpendingCategory(result);
        result.close();

        return category;
    }

    public SpendingCategory addSpendingCategoryToTarget_by_id(int target_id, SpendingCategory category_to_add) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_TARGET_ID, target_id);
        contentValues.put(COLUMN_CATEGORY_NAME, category_to_add.getCategory_name());
        contentValues.put(COLUMN_SPENT_IN_CATEGORY, category_to_add.getSpent_in_category());
        // Insert the data to the table
        int insertID = (int) (db.insert(CATEGORIES_TABLE, null, contentValues));
        Cursor result = db.query(CATEGORIES_TABLE, categoriesAllColumns, COLUMN_CATEGORY_ID + " = " + insertID, null, null, null, null);
        result.moveToFirst();
        SpendingCategory category = cursorToSpendingCategory(result);
        result.close();

        return category;
    }

    public void deleteCategoryById(int category_to_delete_id, Target target){
        SQLiteDatabase db = this.getWritableDatabase();
        // Deleting all the expenses in current category
        db.delete(EXPENSES_TABLE, COLUMN_CATEGORY_ID + " = "  + category_to_delete_id + " AND " + COLUMN_TARGET_ID + " = " + target.getId(), null);
        // Deleting the category itself
        db.delete(CATEGORIES_TABLE, COLUMN_CATEGORY_ID + " = "  + category_to_delete_id, null);
    }

    public void updateTotalSpentInCategory(int category_id, float amount_spent){
        SQLiteDatabase db = this.getReadableDatabase();
        SpendingCategory category = getSpendingCategoryById(category_id);

        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_CATEGORY_ID, category.getCategory_id());
        contentValues.put(COLUMN_TARGET_ID, category.getTarget_id());
        contentValues.put(COLUMN_CATEGORY_NAME, category.getCategory_name());
        contentValues.put(COLUMN_SPENT_IN_CATEGORY, amount_spent);
        db.update(CATEGORIES_TABLE, contentValues, COLUMN_CATEGORY_ID+" = "+category_id, null);
    }

    public void renameSpendingCategory(int category_id, String new_category_name){
        SQLiteDatabase db = this.getReadableDatabase();
        SpendingCategory category = getSpendingCategoryById(category_id);

        ContentValues contentValues = new ContentValues();
        // Fill contentValues with the data
        contentValues.put(COLUMN_CATEGORY_ID, category.getCategory_id());
        contentValues.put(COLUMN_TARGET_ID, category.getTarget_id());
        contentValues.put(COLUMN_CATEGORY_NAME, new_category_name);
        contentValues.put(COLUMN_SPENT_IN_CATEGORY, category.getSpent_in_category());
        db.update(CATEGORIES_TABLE, contentValues, COLUMN_CATEGORY_ID+" = "+category_id, null);
    }

    protected SpendingCategory cursorToSpendingCategory(Cursor cursor){
        SpendingCategory category = new SpendingCategory();
        category.setCategory_id(cursor.getInt(0));
        category.setTarget_id(cursor.getInt(1));
        category.setCategory_name(cursor.getString(2));
        category.setSpent_in_category(cursor.getFloat(3));
        // add other columns here
        return category;
    }
    // ------------------------------- Helpful functions - CATEGORIES ----------------------------------
    public void createSettingsEntry(String language){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SETTINGS_ID, 1);
        contentValues.put(COLUMN_LANGUAGE, language);
        db.insert(SETTINGS_TABLE, null, contentValues);

    }
    public String getLanguage(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.query( SETTINGS_TABLE, settingsAllColumns, COLUMN_SETTINGS_ID + " = 1", null, null, null, null);
        result.moveToFirst();
        String language = result.getString(1);
        return language;
    }

    public void changeLanguage(String given_language){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SETTINGS_ID, 1);
        contentValues.put(COLUMN_LANGUAGE, given_language);
        db.update(SETTINGS_TABLE, contentValues, COLUMN_SETTINGS_ID+" = 1", null);
    }
    public boolean settingsDatabaseEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        String count = "SELECT count(*) FROM " + SETTINGS_TABLE;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int entries = cursor.getInt(0);
        if(entries>0)
            return false;
        else
            return true;
    }
}
