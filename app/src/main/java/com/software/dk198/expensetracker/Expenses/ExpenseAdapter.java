package com.software.dk198.expensetracker.Expenses;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.CategoriesShowingFragment;
import com.software.dk198.expensetracker.Targets.Target;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private static final String TAG = "ExpenseAdapter";
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView amountTextView;
        public TextView descriptionTextView;
        public TextView dateTextView;
        private Context context;
        int target_id;
        int category_id;
        int expense_id;
        public TextView buttonViewOption;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            amountTextView = (TextView) itemView.findViewById(R.id.expenseAmount);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            Log.d(TAG, "item clicked, expense id: " + expense_id);
            int position = getAdapterPosition(); // gets item position
            Intent intent = new Intent(context, ExpenseShowingActivity.class);
            intent.putExtra("expenseId", expense_id);
            intent.putExtra("targetId", target_id);
            intent.putExtra("categoryId", category_id);
            context.startActivity(intent);
        }
    }

    private List<Expense> mExpenses;

    // Pass in the target array into the constructor
    public ExpenseAdapter(List<Expense> expenses) {
        mExpenses = expenses;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View targetView = inflater.inflate(R.layout.item_expense_cardview, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(context, targetView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ExpenseAdapter.ViewHolder viewHolder, int position) {
        final ViewHolder given_view_holder = viewHolder;
        // Get the data model based on position
        final Expense expense = mExpenses.get(position);

        // Set item views based on your views and data model
        TextView amountTextView = viewHolder.amountTextView;
        TextView descriptionTextView = viewHolder.descriptionTextView;
        TextView dateTextView = viewHolder.dateTextView;
        amountTextView.setText(String.format("%,.2f", expense.getAmount()));
        descriptionTextView.setText(expense.getDetails());
        dateTextView.setText(expense.getDate());
//
        viewHolder.target_id = expense.getTarget_id();
        viewHolder.category_id = expense.getCategory_id();
        viewHolder.expense_id = expense.getExpense_id();
        final int expense_id = expense.getExpense_id();
        final int target_id = expense.getTarget_id();
        viewHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(given_view_holder.context, given_view_holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.expense_item_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                edit_expense(given_view_holder.context, expense_id);
                                break;
                            case R.id.remove:
                                remove_expense(given_view_holder.context,target_id , expense_id);
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    private void edit_expense(Context context, int expense_id) {
        Intent intent = new Intent(context, EditExpenseActivity.class);
        intent.putExtra("expenseId", expense_id);
        context.startActivity(intent);
    }


    private void remove_expense(Context context,  int target_id, int expense_id){
        Log.d(TAG, "removing expense id: " + expense_id + " from target id: " + target_id);
        final Context given_context = context;
        final int given_expense_id = expense_id;
        final int given_target_id = target_id;
        // add an "Are you sure?" popup window
        AlertDialog.Builder builder = new AlertDialog.Builder(given_context);
        builder.setCancelable(true);
        builder.setTitle(given_context.getString(R.string.remove)+"?");
        builder.setMessage(given_context.getResources().getString(R.string.expense_deleting_warrning));
        builder.setPositiveButton(given_context.getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBHelper database = new DBHelper(given_context);
                        Expense expense = database.getExpenseById(given_expense_id);
                        Target target = database.getTargetById(given_target_id);
                        SpendingCategory category = database.getSpendingCategoryById(expense.getCategory_id());
                        // removes the expense from the database
                        database.deleteExpense(expense);
                        float expense_amount = expense.getAmount();
                        float new_balance;
                        // Update Category total
                        new_balance = category.getSpent_in_category() - expense_amount;
                        database.updateTotalSpentInCategory(category.getCategory_id(), new_balance);
                        int index = 0;
                        for (SpendingCategory spending_category : CategoriesShowingFragment.differentPaymentCategories) {
                            if (spending_category.getCategory_id() == category.getCategory_id()) {
                                spending_category.setSpent_in_category(new_balance);
                                break;
                            }
                            index++;
                        }
                        CategoriesShowingFragment.adapter.notifyItemChanged(index);
                        CategoriesShowingFragment.categoriesView.scrollToPosition(0);
                        // Update Target total
                        new_balance = target.getTotalSpendings() - expense_amount;
                        database.updateTotalSpentForTarget(target.getId(), new_balance);
                        CategoriesShowingFragment.totalSpent.setText(given_context.getResources().getString(R.string.total) + new_balance);

                        // Remove from ShowPaymentsInCategoryActivity
                        index = 0;
                        for (Expense expense_item: ShowPaymentsInCategoryActivity.expenses){
                            if (expense_item.getExpense_id() == given_expense_id)
                                break;
                            index++;
                        }
                        ShowPaymentsInCategoryActivity.expenses.remove(index);
                        ShowPaymentsInCategoryActivity.adapter.notifyItemRemoved(index);
                        ShowPaymentsInCategoryActivity.adapter.notifyItemRangeChanged(index, ShowPaymentsInCategoryActivity.expenses.size());
                        ShowPaymentsInCategoryActivity.expensesView.scrollToPosition(0);
                        if (ShowPaymentsInCategoryActivity.expenses.size() == 0){
                            ShowPaymentsInCategoryActivity.centerOfScreenTextView.setVisibility(View.VISIBLE);
                        }
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
}