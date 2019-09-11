package com.software.dk198.expensetracker.DataBackup;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.software.dk198.expensetracker.R;

public class CustomListAdapter extends ArrayAdapter {
    private final Activity context;

    //to store the list of countries
    private final String[] nameArray;

    //to store the spendings
    private final Float[] spendingArray;

    //to store the target IDs
    private final Integer[] idArray;

    public CustomListAdapter(Activity context, String[] nameArrayParam, Float[] spendingArrayParam, Integer[] idArrayParam){

        super(context,R.layout.item_target_import_listview , nameArrayParam);

        this.context=context;
        this.spendingArray = spendingArrayParam;
        this.nameArray = nameArrayParam;
        this.idArray = idArrayParam;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_target_import_listview, null,true);

        //this code gets references to objects in the listview_row.xml file
        TextView nameTextField = (TextView) rowView.findViewById(R.id.targetName);
        TextView amount = (TextView) rowView.findViewById(R.id.targetAmount);

        //this code sets the values of the objects to values from the arrays
        nameTextField.setText(nameArray[position]);
        amount.setText(Float.toString(spendingArray[position]));

        return rowView;

    };

}
