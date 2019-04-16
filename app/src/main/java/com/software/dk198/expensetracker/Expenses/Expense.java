package com.software.dk198.expensetracker.Expenses;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Expense {
    private int expense_id;
    private int target_id;
    private int category_id;
    private String date;
    private String details;
    private Float amount;

    // When getting data from the database
    public Expense(){
    }
    // When date is given
    public Expense(int expense_id, int target_id, int category_id, String date, String details, Float amount){
        this.expense_id = expense_id;
        this.target_id = target_id;
        this.category_id = category_id;
        this.date = date;
        this.details = details;
        this.amount = amount;
    }
    // When no date was given
    public Expense(int expense_id, int target_id, int category_id, String details, Float amount){
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.expense_id = expense_id;
        this.target_id = target_id;
        this.category_id = category_id;
        this.date = date;
        this.details = details;
        this.amount = amount;
    }


    public int getExpense_id() {
        return expense_id;
    }
    public void setExpense_id(int expense_id) {
        this.expense_id = expense_id;
    }
    public int getTarget_id() {
        return target_id;
    }
    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }
    public int getCategory_id() {
        return category_id;
    }
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
