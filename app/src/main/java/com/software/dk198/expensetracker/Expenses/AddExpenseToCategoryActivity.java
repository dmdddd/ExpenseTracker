package com.software.dk198.expensetracker.Expenses;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;

import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.CategoriesShowingFragment;
import com.software.dk198.expensetracker.Targets.Target;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseToCategoryActivity extends AppCompatActivity {
    int target_id;
    int category_id;
    DBHelper database;
    Button addButton;
    TextView informationTextView;
    EditText date;
    Context context;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense_to_category);

        context = this;
        database = new DBHelper(this);
        Bundle extras = getIntent().getExtras();
        target_id = extras.getInt("targetId");
        category_id = extras.getInt("categoryId");

        // Init GUI elements
        final EditText amount = (EditText) findViewById(R.id.amountEditText);
        date = (EditText) findViewById(R.id.dateEditText);
        final EditText details = (EditText) findViewById(R.id.detailsEditText);
        amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        informationTextView = (TextView) findViewById(R.id.informationTextView);
        String target_name = database.getTargetById(target_id).getName();
        String category_name = database.getSpendingCategoryById(category_id).getCategory_name();
        updateLabel();

        // Adding to X in Y
        informationTextView.setText(getString(R.string.adding_to) + " " + category_name + " " + getString(R.string.in) + " " + target_name);
        addButton = (Button) findViewById(R.id.addExpenseButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amount.getText().toString().length()>0 && Float.valueOf(amount.getText().toString())>0){
                    // Updating a new expense added in the ShowPaymentsInCategoryActivity
                    Expense new_expense  = database.insertExpense(target_id, category_id, Float.valueOf(amount.getText().toString()), date.getText().toString(), details.getText().toString());
                    if(ShowPaymentsInCategoryActivity.active) { // If the activity has been loaded, update it
                        ShowPaymentsInCategoryActivity.expenses.add(0, new_expense);
                        ShowPaymentsInCategoryActivity.adapter.notifyItemInserted(0); // Notify the adapter that an item was inserted at position 0
                        ShowPaymentsInCategoryActivity.expensesView.scrollToPosition(0);// Moves the recycler view to the top again, so the newly created item would be seen
//                        TextView textView = findViewById(R.id.centerOfScreenTextView);
//                        textView.setVisibility(View.INVISIBLE);
                        ShowPaymentsInCategoryActivity.centerOfScreenTextView.setVisibility(View.INVISIBLE);

                    }
                    // Updating the total spent in category
                    SpendingCategory category_to_replace = database.getSpendingCategoryById(category_id);
                    float old_total_for_category = database.getSpendingCategoryById(category_id).getSpent_in_category();
                    float new_total_for_category = old_total_for_category + Float.valueOf(amount.getText().toString());
                    database.updateTotalSpentInCategory(category_id, new_total_for_category);
                    SpendingCategory updated_category = database.getSpendingCategoryById(category_id);
                    // Update the target's total shown in the CategoriesShowingFragment
                    for (SpendingCategory category : CategoriesShowingFragment.differentPaymentCategories)
                        if (category.getCategory_id()==category_id)
                            category.setSpent_in_category(new_total_for_category);

                    CategoriesShowingFragment.adapter.notifyDataSetChanged();

                    // Updating the total spent for target(including the textView)
                    Target target = database.getTargetById(target_id);
                    float old_total_for_target = target.getTotalSpendings();
                    float new_total_for_target = old_total_for_target + Float.valueOf(amount.getText().toString());
                    database.updateTotalSpentForTarget(target_id, new_total_for_target);
                    String currency_symbol = target.getDefault_currency();
                    CategoriesShowingFragment.totalSpent.setText(getString(R.string.total) + " " + String.format("%,.2f", new_total_for_target)+currency_symbol);

                    CategoriesShowingFragment.differentPaymentCategories.remove(database);

                    // Closing current activity
                    finish();
                }
                else{
                    Toast.makeText(AddExpenseToCategoryActivity.this, getString(R.string.no_amount_given), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Date picker
        final DatePickerDialog.OnDateSetListener given_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, given_date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(myCalendar.getTime()));
    }
}
