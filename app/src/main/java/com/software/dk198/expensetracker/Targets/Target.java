package com.software.dk198.expensetracker.Targets;

import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;

import java.util.ArrayList;

public class Target {
    private String name;
    //    public List<Expense> expenses;
    public ArrayList<SpendingCategory> differentPaymentCategories;
    public Float totalSpendings;
    private Integer target_id;
    private String default_currency;
    private String pieChartColor;

    public Target(){
        this.name = "";
        this.pieChartColor = "Liberty";
        differentPaymentCategories = new ArrayList<>();
    }

    public Target(String name){
        this.name = name;
        this.pieChartColor = "Liberty";
        differentPaymentCategories = new ArrayList<>();
    }

    // Constructor used when pulling data from SQLite
    public Target(String name, Integer target_id){
        setName(name);
        this.differentPaymentCategories = new ArrayList<>();
        this.pieChartColor = "Liberty";
        setId(target_id);
    }

    public Target(String name, ArrayList<SpendingCategory> categories){
        this.name = name;
//        expenses = new ArrayList<>();
        differentPaymentCategories = categories;
    }

    public String getName() {
        return name;
    }
    public Integer getId() { return target_id; }
    public Float getTotalSpendings() { return totalSpendings; }

//    public void addPayment(Payment new_payment){
//        this.payments.add(new_payment);
//        this.totalSpendings += new_payment.amount;
//        if(!differentPaymentCategories.contains(new_payment.name))
//            differentPaymentCategories.add(new_payment.name);
//    }

    public String toString(){
        return this.getName();
    }


    // Getters
    public void setName(String new_name){
        this.name = new_name;
    }
    public void setId(Integer new_id){
        this.target_id = new_id;
    }
    public void setTotalSpent(Float spent){
        this.totalSpendings = spent;
    }
    public void setDifferentSpendingCategories(ArrayList<SpendingCategory> categories){ this.differentPaymentCategories = categories; }
    public void setDefault_currency(String default_currency) { this.default_currency = default_currency; }
    public void setPieChartColor(String pieChartColor) {this.pieChartColor = pieChartColor; }
    // Setters
    public String getDefault_currency() { return default_currency; }
    public String getPieChartColor() { return pieChartColor; }



    @Override
    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }

        if (!Target.class.isAssignableFrom(target.getClass())) {
            return false;
        }

        final Target other = (Target) target;
        if ((this.target_id == null) ? (other.target_id != null) : !this.target_id.equals(other.target_id)) {
            return false;
        }

        if (this.getId() != other.target_id) {
            return false;
        }

        return true;
    }
}
