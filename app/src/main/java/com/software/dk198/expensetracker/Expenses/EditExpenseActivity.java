package com.software.dk198.expensetracker.Expenses;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.software.dk198.expensetracker.Targets.CategoriesShowingFragment;
import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.Target;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {
    private static final String TAG = "EditExpenseActivity";
    DBHelper database;
    int expense_id;
    Expense expense;
    EditText amount;
    EditText date;
    EditText details;
    TextView informationTextView;
    Button saveChangesButton;
    Context context;
    final Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        Bundle extras = getIntent().getExtras();
        expense_id = extras.getInt("expenseId");
        context = this;

        // GUI initializations
        informationTextView = (TextView) findViewById(R.id.informationTextView);
        amount = (EditText) findViewById(R.id.amountEditText);
        date = (EditText) findViewById(R.id.dateEditText);
        details = (EditText) findViewById(R.id.detailsEditText);
        saveChangesButton = (Button) findViewById(R.id.saveChangesButton);
        amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        database = new DBHelper(this);
        expense = database.getExpenseById(expense_id);
        String target_name = database.getTargetById(expense.getTarget_id()).getName();
        String category_name = database.getSpendingCategoryById(expense.getCategory_id()).getCategory_name();
        informationTextView.setText(getString(R.string.editing) + " " + category_name + " " + getString(R.string.in) + " " + target_name);
        amount.setText(expense.getAmount().toString());
        date.setText(expense.getDate());
        amount.setSelectAllOnFocus(true);
        if (expense.getDetails().length()>0)
            details.setText(expense.getDetails());
        else
            details.setHint(getString(R.string.no_details_were_given));

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amount.getText().toString().length()>0 || date.getText().toString().length()>0 || details.getText().toString().length()>0){
                    Log.d(TAG, "Saving changes for expense number " + expense_id);
                    // Getting data from GUI
                    Expense updated_expense = database.getExpenseById(expense_id);
                    String given_amount = amount.getText().toString();
                    String given_date = date.getText().toString();
                    String given_details = details.getText().toString();
                    // Update details only if they were given
                    if (given_amount.length()>0 && Float.valueOf(given_amount)>0) {
                        updated_expense.setAmount(Float.valueOf(given_amount));
                        Log.d(TAG, "Amount set to: " + given_amount);
                    }
                    if (given_date.length()>0) {
                        updated_expense.setDate(given_date);
                        Log.d(TAG, "Date set to: " + given_date);
                    }
                    if (given_details.length()>0) {
                        updated_expense.setDetails(given_details);
                        Log.d(TAG, "Details set to: " + given_details);
                    }

                    // Updating the expense
                    database.updateExpense(updated_expense);

                    //ToDo
                    // Update total spent in category
                    SpendingCategory category = database.getSpendingCategoryById(expense.getCategory_id());
                    Float updated_spending_in_category = category.getSpent_in_category()- expense.getAmount()+updated_expense.getAmount();
                    database.updateTotalSpentInCategory(category.getCategory_id(), updated_spending_in_category);
                    // Update total shown for the category in CategoriesShowingFragment's view holder
                    int index = 0;
                    for (SpendingCategory sCategory : CategoriesShowingFragment.differentPaymentCategories){
                        if (sCategory.getCategory_id() == category.getCategory_id()){
                            sCategory.setSpent_in_category(updated_spending_in_category);
                            break;
                        }
                        index++;
                    }
                    CategoriesShowingFragment.adapter.notifyItemChanged(index);

                    // Update total spent for target
                    Target target = database.getTargetById(expense.getTarget_id());
                    Float updated_spending_in_target = target.getTotalSpendings() - expense.getAmount() + updated_expense.getAmount();
                    database.updateTotalSpentForTarget(target.getId(), updated_spending_in_target);
                    // Updating "Total: " in CategoriesShowingFragment
                    String currency_symbol = target.getDefault_currency();
                    CategoriesShowingFragment.totalSpent.setText(getString(R.string.total) + " " + String.format("%,.2f", updated_spending_in_target)+currency_symbol);
                    // Updating the edited expense in ShowPaymentsInCategoryActivity
                    index = 0;
                    for (Expense sExpense : ShowPaymentsInCategoryActivity.expenses){
                        if (sExpense.getExpense_id() == expense_id){
                            sExpense.setAmount(updated_expense.getAmount());
                            sExpense.setDate(updated_expense.getDate());
                            sExpense.setDetails(updated_expense.getDetails());
                            break;
                        }
                        index++;
                    }
                    ShowPaymentsInCategoryActivity.adapter.notifyItemChanged(index);

                    // Closing current activity
                    finish();
                }
                else{
                    Toast.makeText(EditExpenseActivity.this, getString(R.string.nothing_to_update), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Date Picker
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, given_date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }


    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(myCalendar.getTime()));
    }
}
