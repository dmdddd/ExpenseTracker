package com.software.dk198.expensetracker.Targets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.MainActivity;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.Targets.CategoriesShowingFragment;
import com.software.dk198.expensetracker.Targets.Target;

public class TargetSettingsFragment extends Fragment{

    DBHelper database;
    private Spinner currencySpinner;
    private Spinner chartColorSpinner;
    private Button saveChangesButton;
    private EditText targetName;
    private Target target;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_target_settings, null);

        database = new DBHelper(view.getContext());
        Bundle extras = getActivity().getIntent().getExtras();
        String target_name = extras.getString("targetName");
        int target_id = extras.getInt("targetId");
        target = database.getTargetById(target_id);
        targetName = (EditText) view.findViewById(R.id.targetNameEditText);
        targetName.setHint(target.getName());
        // Setting the currency spinner
        currencySpinner = view.findViewById(R.id.currencySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, MainActivity.currencies);
        currencySpinner.setAdapter(adapter);
        String default_currency = target.getDefault_currency();
        if (default_currency.equals("")) default_currency = "None";
        currencySpinner.setSelection(adapter.getPosition(default_currency));
        // Setting the chart color spinner
        chartColorSpinner = view.findViewById(R.id.chartColorSpinner);
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, MainActivity.pie_chart_colors);
        chartColorSpinner.setAdapter(colorAdapter);
        chartColorSpinner.setSelection(colorAdapter.getPosition(target.getPieChartColor()));

        saveChangesButton = (Button) view.findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int name_has_been_changed = 0;
                if(targetName.getText().toString().length()>0){ //Changing Target's name
                    target.setName(targetName.getText().toString());
                    name_has_been_changed = 1;
                }//Else: Not changing Target's name
                String default_currency = currencySpinner.getSelectedItem().toString();
                if (currencySpinner.getSelectedItem().toString().equals("None")) default_currency = "";
                target.setDefault_currency(default_currency);
                target.setPieChartColor(chartColorSpinner.getSelectedItem().toString());
                // Update target's entry in the database
                database.updateTarget(target);
                // Update the currency sign in CategoryShowingFragment
                CategoriesShowingFragment.totalSpent.setText("Total: " + target.getTotalSpendings()+target.getDefault_currency());
                if (name_has_been_changed == 1){
                    int index = 0;
                    for (Target target_in_main : MainActivity.targets){
                        if (target_in_main.getId() == target.getId())
                            break;
                        index++;
                    }
                    MainActivity.targets.get(index).setName(targetName.getText().toString());
                    MainActivity.adapter.notifyItemChanged(index);
                    CategoriesShowingFragment.targetName.setText(targetName.getText().toString());
                    name_has_been_changed = 0;
                }
                Toast.makeText(getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();

            }
        });


        return view;
    }

}
