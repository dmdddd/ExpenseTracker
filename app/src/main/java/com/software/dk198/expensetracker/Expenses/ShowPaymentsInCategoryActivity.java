package com.software.dk198.expensetracker.Expenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.CategoriesShowingFragment;
import com.software.dk198.expensetracker.Targets.Target;

import java.util.ArrayList;

public class ShowPaymentsInCategoryActivity extends AppCompatActivity {
    public static boolean active = false;
    int target_id;
    int category_id;
    public static ArrayList<Expense> expenses;
    public static TextView centerOfScreenTextView;
    Target target;
    SpendingCategory category;
    Button addPaymentButton;
    Button removeCategoryButton;
    DBHelper database;
    public static ExpenseAdapter adapter;
    public static RecyclerView expensesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_payments_in_category);
        active = true;
        database = new DBHelper(this);
        Bundle extras = getIntent().getExtras();
        target_id = extras.getInt("targetId");
        category_id = extras.getInt("categoryId");
        final TextView targetNameTextView = (TextView) findViewById(R.id.targetNameTextView);

        centerOfScreenTextView = findViewById(R.id.centerOfScreenTextView);
        target = database.getTargetById(target_id);
        category = database.getSpendingCategoryById(category_id);
        targetNameTextView.setText(target.getName() + " - " + category.getCategory_name());
        // Add payment button
        addPaymentButton = (Button) findViewById(R.id.addPaymentButton);
        addPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowPaymentsInCategoryActivity.this, AddExpenseToCategoryActivity.class);
                intent.putExtra("targetId", target_id);
                intent.putExtra("categoryId", category_id);
                startActivity(intent);
            }
        });

        // Remove category button
        removeCategoryButton = (Button) findViewById(R.id.removeCategoryButton);
        removeCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add an "Are you sure?" popup window
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowPaymentsInCategoryActivity.this);
                builder.setCancelable(true);
                builder.setTitle(getString(R.string.remove) + "?");
                builder.setMessage(getString(R.string.category_deleting_warrning));
                builder.setPositiveButton(getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Updating total spent for the target
                                float new_amount = database.getTargetById(target_id).getTotalSpendings() - database.getSpendingCategoryById(category_id).getSpent_in_category();
                                database.updateTotalSpentForTarget(target_id, new_amount);
                                CategoriesShowingFragment.totalSpent.setText(getString(R.string.total) + new_amount);
                                // Removes the category from the database
                                database.deleteCategoryById(category_id, target);
                                //Remove category from the target displayed in CategoriesShowingFragment
                                int index = 0;
                                for (SpendingCategory category : CategoriesShowingFragment.differentPaymentCategories){
                                    if (category.getCategory_id() == category_id){
                                        break;
                                    }
                                    index++;
                                }
                                CategoriesShowingFragment.differentPaymentCategories.remove(index);
                                CategoriesShowingFragment.adapter.notifyItemRemoved(index);
                                CategoriesShowingFragment.adapter.notifyItemRangeChanged(index, CategoriesShowingFragment.differentPaymentCategories.size());
                                CategoriesShowingFragment.categoriesView.scrollToPosition(0);
                                // Closing the current activity
                                ((Activity) ShowPaymentsInCategoryActivity.this).finish();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // updating the expenses
        expenses = database.getExpensesByTargetIdAndCategoryId(target_id, category_id);
        if (expenses.size()>0){
            centerOfScreenTextView.setVisibility(View.INVISIBLE);
        }
        // RecyclerView
        expensesView = (RecyclerView) findViewById(R.id.expensesRecyclerView);

        // Create adapter passing in the data
        adapter = new ExpenseAdapter(expenses);
        // Attach the adapter to the recyclerview to populate items
        expensesView.setAdapter(adapter);
        // Set layout manager to position the items
        expensesView.setLayoutManager(new LinearLayoutManager(this));

    }
}
