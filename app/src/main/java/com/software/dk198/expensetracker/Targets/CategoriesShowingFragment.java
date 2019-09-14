package com.software.dk198.expensetracker.Targets;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.software.dk198.expensetracker.SpendingCategories.CategoryAdapter;
import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;

import java.util.ArrayList;

public class CategoriesShowingFragment extends Fragment {

    private static final String TAG = "CategoriesShowingFrag";
    DBHelper database;
    private Context context;
    private TextView mTextMessage;
    static TextView targetName;
    public static TextView totalSpent;
    static Target target;
    public static ArrayList<SpendingCategory> differentPaymentCategories;
    private int target_id;
    // RecyclerView items
    public static CategoryAdapter adapter;
    public static RecyclerView categoriesView;
    private RecyclerView.LayoutManager categoriesLayoutManager;
    public static TextView noCategoriesTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_categories, null);

        Bundle extras = getActivity().getIntent().getExtras();
        String target_name = extras.getString("targetName");
        target_id = extras.getInt("targetId");
        Log.d(TAG, "Target id recieved: " + target_id);

        context = getActivity();
        database = new DBHelper(context);

        noCategoriesTextView = (TextView) view.findViewById(R.id.noCategoriesTextView);
        Button addCategoryButton = (Button) view.findViewById(R.id.buttonAddSpendingCategory);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked add Category Btn");

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.specify_category_name));

                // Set up the input
                final EditText input = new EditText(context);
                // Specify the type of input expected;
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String new_category_name = input.getText().toString();
                        if (new_category_name.length()>0) {
                            SpendingCategory new_category;
                            // Adding a new Category to the database
                            new_category = database.addSpendingCategoryToTarget(CategoriesShowingFragment.target, new_category_name);
                            // Adding a new category to the list in TargetShowingActivity
                            CategoriesShowingFragment.target.differentPaymentCategories.add(0, new_category);
                            // Notify TargetShowingActivity's RecyclerView that a change has been made
                            CategoriesShowingFragment.adapter.notifyItemInserted(0); // Notify the adapter that an item was inserted at position 0
                            CategoriesShowingFragment.categoriesView.scrollToPosition(0);

                            // Hide "no categories" massage if there are categories to display
                            if (differentPaymentCategories.size()>0){
                                noCategoriesTextView.setVisibility(View.INVISIBLE);
                            }
                        }
                        else
                            Toast.makeText(context, getString(R.string.no_name_given), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        mTextMessage = (TextView) view.findViewById(R.id.targetNameTextView);

        // Looking for the correct target with all it's info
        target = database.getTargetById(target_id);
        differentPaymentCategories = target.differentPaymentCategories;
        // Setting text view's content
        targetName = (TextView) view.findViewById(R.id.targetNameTextView);
        targetName.setText(target_name);
        totalSpent = (TextView) view.findViewById(R.id.totalSpentTextView);
        totalSpent.setText(getString(R.string.total) + " " + String.format("%,.2f", target.getTotalSpendings())+target.getDefault_currency());
        // RecyclerView
        categoriesView = (RecyclerView) view.findViewById(R.id.categoryRecyclerView);

        // Create adapter passing in the data
        adapter = new CategoryAdapter(differentPaymentCategories);
        // Attach the adapter to the recyclerview to populate items
        categoriesView.setAdapter(adapter);
        // Set layout manager to position the items
        categoriesView.setLayoutManager(new LinearLayoutManager(context));

        // Hide "no categories" massage if there are categories to display
        if (differentPaymentCategories.size()>0){
            noCategoriesTextView.setVisibility(View.INVISIBLE);
        }


        return view;
    }
}
