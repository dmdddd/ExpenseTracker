package com.software.dk198.expensetracker.Targets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.MainActivity;
import com.software.dk198.expensetracker.R;

import java.util.ArrayList;

public class AddTargetActivity extends AppCompatActivity {
    private static final String TAG = "AddTargetActivity";
    DBHelper database;
    private Spinner defaultCurrencySpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.target_adding_layout);

        database = new DBHelper(this);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnAdd = (Button) findViewById(R.id.btnAddNewTarget);
        final CheckBox checkBoxFood = (CheckBox) findViewById(R.id.checkBoxFood);
        final CheckBox checkBoxShopping = (CheckBox) findViewById(R.id.checkBoxShopping);
        final CheckBox checkBoxAccommodation = (CheckBox) findViewById(R.id.checkBoxAccommodation);
        final CheckBox checkBoxDrinks = (CheckBox) findViewById(R.id.checkBoxDrinks);
        final CheckBox checkBoxGas = (CheckBox) findViewById(R.id.checkBoxGas);
        final EditText targetName = (EditText) findViewById(R.id.enterNameField);
        defaultCurrencySpinner = (Spinner) findViewById(R.id.defaultCurrencySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, MainActivity.currencies);
        //set the spinners adapter to the previously created one.
        defaultCurrencySpinner.setAdapter(adapter);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Add a new contact
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if a name is given, create a new target
                if (targetName.getText().toString().length()>0) {
                    ArrayList<String> categories = new ArrayList<>();
                    // Adding the target to the database
                    String default_currency = defaultCurrencySpinner.getSelectedItem().toString();
                    if (default_currency.equals("None"))
                        default_currency = "";
                    Target added_target = database.insertTarget(targetName.getText().toString(), default_currency);
                    if (added_target != null){
                        // Gathering information from the screen(target name and selected boxes)
                        if (checkBoxFood.isChecked()) database.addSpendingCategoryToTarget(added_target, getString(R.string.food));
                        if (checkBoxShopping.isChecked()) database.addSpendingCategoryToTarget(added_target, getString(R.string.shopping));
                        if (checkBoxAccommodation.isChecked()) database.addSpendingCategoryToTarget(added_target, getString(R.string.accommodation));
                        if (checkBoxDrinks.isChecked()) database.addSpendingCategoryToTarget(added_target, getString(R.string.drinks));
                        if (checkBoxGas.isChecked()) database.addSpendingCategoryToTarget(added_target, getString(R.string.gas));

                        // Adding the target the the target array list in MainActivity
                        MainActivity.targets.add(0, added_target);
                        MainActivity.adapter.notifyItemInserted(0); // Notify the adapter that an item was inserted at position 0
                        MainActivity.targetsView.scrollToPosition(0);       // Moves the recycler view to the top again, so the newly created item would be seen

                        // Hide "no subjects" message
                        if (MainActivity.targets.size() > 0){
                            MainActivity.noSubjectsTextView.setVisibility(View.INVISIBLE);
                        }
                    }
                    finish();
                }
                else
                    Toast.makeText(AddTargetActivity.this, "Please insert a name", Toast.LENGTH_SHORT).show();
            }});
    }
}
