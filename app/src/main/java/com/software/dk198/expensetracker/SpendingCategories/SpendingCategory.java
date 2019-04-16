package com.software.dk198.expensetracker.SpendingCategories;

import com.software.dk198.expensetracker.Targets.Target;

public class SpendingCategory {
    private String category_name;
    private int category_id;
    private int target_id;
    private float spent_in_category;

    public SpendingCategory(){
        this.spent_in_category = 0;
    }
    // For getting a created one from the database
    public SpendingCategory(String name, int target, int cat_id, float spent){
        this.category_name = name;
        this.category_id = cat_id;
        this.target_id = target;
        this.spent_in_category = spent;
    }
    // For a newly created item
    public SpendingCategory(String name, int target, int cat_id){
        this.category_name = name;
        this.category_id = cat_id;
        this.target_id = target;
        this.spent_in_category = 0;
    }


    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getTarget_id() {
        return target_id;
    }

    public void setTarget_id(int target_id) {
        this.target_id = target_id;
    }

    public float getSpent_in_category() {
        return spent_in_category;
    }

    public void setSpent_in_category(float spent_in_category) {
        this.spent_in_category = spent_in_category;
    }

    public String toString(){
        return "Category name: " + this.getCategory_name() + " ,Category ID: " + this.category_id + "| ";
    }


    @Override
    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }

        if (!Target.class.isAssignableFrom(target.getClass())) {
            return false;
        }

        SpendingCategory other = (SpendingCategory) target;
//        if ((this.category_id == null) ? (other.category_id != null) : !this.category_id.equals(other.category_id)) {
//            return false;
//        }

//        if (this.category_id != other.category_id) {
//            return false;
//        }

//        return true;
        return this.category_id == other.category_id;
    }
}
