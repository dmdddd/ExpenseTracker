package com.software.dk198.expensetracker.Expenses;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.software.dk198.expensetracker.Targets.CategoriesShowingFragment;
import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.Target;

public class ExpenseShowingActivity extends AppCompatActivity {

    int expense_id;
    int target_id;
    int spending_category_id;
    Expense expense;
    SpendingCategory category;
    Target target;
    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expense);

        database = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        expense_id = extras.getInt("expenseId");
        target_id = extras.getInt("targetId");

        // Getting data
        target = database.getTargetById(target_id);
        expense = database.getExpenseById(expense_id);
        category = database.getSpendingCategoryById(expense.getCategory_id());
        spending_category_id = category.getCategory_id();
        // Setting TextViews
        TextView targetTextView = (TextView) findViewById(R.id.targetTextView);
        targetTextView.setText(target.getName());
        TextView categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        categoryTextView.setText(category.getCategory_name());
        TextView amountTextView = (TextView) findViewById(R.id.amountTextView);
        amountTextView.setText(String.format("%,.2f", expense.getAmount())+target.getDefault_currency());
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        dateTextView.setText(expense.getDate());
        TextView detailsTextView = (TextView) findViewById(R.id.detailsTextView);
        detailsTextView.setText(expense.getDetails());
        // Handling the remove expense button
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add an "Are you sure?" popup window
                AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseShowingActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Delete?");
                builder.setMessage(getString(R.string.expense_deleting_warrning));
                builder.setPositiveButton(getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // removes the expense from the database
                                database.deleteExpense(expense);
                                float expense_amount = expense.getAmount();
                                float new_balance;
                                // Update Category total
                                new_balance = category.getSpent_in_category() - expense_amount;
                                database.updateTotalSpentInCategory(category.getCategory_id(), new_balance);
                                int index = 0;
                                for (SpendingCategory category : CategoriesShowingFragment.differentPaymentCategories) {
                                    if (category.getCategory_id() == spending_category_id) {
                                        category.setSpent_in_category(new_balance);
                                        break;
                                    }
                                    index++;
                                }
                                CategoriesShowingFragment.adapter.notifyItemChanged(index);
                                CategoriesShowingFragment.categoriesView.scrollToPosition(0);
                                // Update Target total
                                new_balance = target.getTotalSpendings() - expense_amount;
                                database.updateTotalSpentForTarget(target.getId(), new_balance);
                                CategoriesShowingFragment.totalSpent.setText(getString(R.string.total) + new_balance);

                                // Remove from ShowPaymentsInCategoryActivity
                                index = 0;
                                for (Expense expense: ShowPaymentsInCategoryActivity.expenses){
                                    if (expense.getExpense_id() == expense_id)
                                        break;
                                    index++;
                                }
                                ShowPaymentsInCategoryActivity.expenses.remove(index);
                                ShowPaymentsInCategoryActivity.adapter.notifyItemRemoved(index);
                                ShowPaymentsInCategoryActivity.adapter.notifyItemRangeChanged(index, ShowPaymentsInCategoryActivity.expenses.size());
                                ShowPaymentsInCategoryActivity.expensesView.scrollToPosition(0);

                                // Closing the current activity
                                ((Activity) ExpenseShowingActivity.this).finish();
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
    }
}
