package com.software.dk198.expensetracker.SpendingCategories;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.Expenses.AddExpenseToCategoryActivity;
import com.software.dk198.expensetracker.MainActivity;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.Expenses.ShowPaymentsInCategoryActivity;
import com.software.dk198.expensetracker.Targets.CategoriesShowingFragment;
import com.software.dk198.expensetracker.Targets.Target;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static final String TAG = "CategoryAdapter";
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView categoryName;
        public TextView totalSpent;
        public Button addButton;
        private Context context;
        public TextView buttonViewOption;
        int target_id;
        int category_id;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            categoryName = (TextView) itemView.findViewById(R.id.spendingCategoryName);
            totalSpent = (TextView) itemView.findViewById(R.id.totalSpentTextView);
            addButton = (Button) itemView.findViewById(R.id.addButton);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            Intent intent = new Intent(context, ShowPaymentsInCategoryActivity.class);
            intent.putExtra("targetId", target_id);
            intent.putExtra("categoryId", category_id);
            context.startActivity(intent);
        }
    }

    private List<SpendingCategory> spending_categories;

    // Pass in the different categories array into the constructor
    public CategoryAdapter(List<SpendingCategory> categories) {
        spending_categories = categories;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View categoryView = inflater.inflate(R.layout.item_spending_category_cardview, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(context, categoryView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder viewHolder, int position) {
        final ViewHolder given_view_holder = viewHolder;
        // Get the data model based on position
        final SpendingCategory category = spending_categories.get(position);

        // Set item views based on your views and data model
        TextView categoryName = viewHolder.categoryName;
        categoryName.setText(category.getCategory_name());

        viewHolder.target_id = category.getTarget_id();
        viewHolder.category_id = category.getCategory_id();
        final int target_i = category.getTarget_id();
        final int cat_i = category.getCategory_id();
        TextView totalSpent = viewHolder.totalSpent;
        totalSpent.setText(viewHolder.context.getString(R.string.total) + " " +String.format("%,.2f", category.getSpent_in_category()));
        viewHolder.addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(view.getContext(), AddExpenseToCategoryActivity.class);
                intent.putExtra("targetId", target_i);
                intent.putExtra("categoryId", cat_i);
                view.getContext().startActivity(intent);
            }
        });

        viewHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(given_view_holder.context, given_view_holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.category_menu_in_cardview);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.rename:
                                update_category_name(given_view_holder.context, category.getCategory_name(), category.getCategory_id());
                                break;
                            case R.id.remove:
                                remove_category(given_view_holder.context, target_i,category.getCategory_id());
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
        return spending_categories.size();
    }

    private void remove_category(Context context, int target_id, int category_id){
        final Context given_context = context;
        final int given_target_id = target_id;
        final int given_category_id = category_id;
        // add an "Are you sure?" popup window
        AlertDialog.Builder builder = new AlertDialog.Builder(given_context);
        builder.setCancelable(true);
        builder.setTitle(given_context.getResources().getString(R.string.remove) + "?");
        builder.setMessage(given_context.getResources().getString(R.string.category_deleting_warrning));
        builder.setPositiveButton(given_context.getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Updating total spent for the target
                        DBHelper database = new DBHelper(given_context);
                        Target target = database.getTargetById(given_target_id);
                        float new_amount = database.getTargetById(given_target_id).getTotalSpendings() - database.getSpendingCategoryById(given_category_id).getSpent_in_category();
                        database.updateTotalSpentForTarget(given_target_id, new_amount);
                        CategoriesShowingFragment.totalSpent.setText(given_context.getResources().getString(R.string.total) + new_amount);
                        // Removes the category from the database
                        database.deleteCategoryById(given_category_id, target);
                        //Remove category from the target displayed in CategoriesShowingFragment
                        int index = 0;
                        for (SpendingCategory category : CategoriesShowingFragment.differentPaymentCategories){
                            if (category.getCategory_id() == given_category_id){
                                break;
                            }
                            index++;
                        }
                        CategoriesShowingFragment.differentPaymentCategories.remove(index);
                        CategoriesShowingFragment.adapter.notifyItemRemoved(index);
                        CategoriesShowingFragment.adapter.notifyItemRangeChanged(index, CategoriesShowingFragment.differentPaymentCategories.size());
                        CategoriesShowingFragment.categoriesView.scrollToPosition(0);

                        // If the last one was deleted, show "no categories" text view
                        if (CategoriesShowingFragment.differentPaymentCategories.size() == 0){
                            CategoriesShowingFragment.noCategoriesTextView.setVisibility(View.VISIBLE);
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
    private void update_category_name(Context context, String current_name, int category_id){
        //update category AND update TARGET(it has it under different categories)

        final Context given_context = context;
        final int given_category_id = category_id;
        AlertDialog.Builder builder = new AlertDialog.Builder(given_context);
        builder.setTitle(given_context.getString(R.string.new_name));
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(given_context).inflate(R.layout.rename_target_popup, null);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.newNamePlainText);
        input.setText(current_name);
        input.setSelectAllOnFocus(true);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DBHelper database = new DBHelper(given_context);
                SpendingCategory category = database.getSpendingCategoryById(given_category_id);
                if(input.getText().toString().length()>0){ //Changing Category's name
                    // Renaming the SpendingCategory in the database
                    database.renameSpendingCategory(given_category_id, input.getText().toString());
                    int index = 0;
                    for (SpendingCategory category_in_activity : CategoriesShowingFragment.differentPaymentCategories){
                        if (category_in_activity.getCategory_id() == given_category_id)
                            break;
                        index++;
                    }
                    CategoriesShowingFragment.differentPaymentCategories.get(index).setCategory_name(input.getText().toString());
                    CategoriesShowingFragment.adapter.notifyItemChanged(index);

//                    target.setName(input.getText().toString());
//                    // Update target's entry in the database
//                    database.updateTarget(target);
//                    int index = 0;
//                    for (Target target_in_main : MainActivity.targets){
//                        if (target_in_main.getId() == target.getId())
//                            break;
//                        index++;
//                    }
//                    MainActivity.targets.get(index).setName(target.getName());
//                    MainActivity.adapter.notifyItemChanged(index);
////                                            CategoriesShowingFragment.targetName.setText(target.getName());
                }//Else: Not changing Target's name
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
}