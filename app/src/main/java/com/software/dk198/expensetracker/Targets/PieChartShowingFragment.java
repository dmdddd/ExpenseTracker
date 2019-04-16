package com.software.dk198.expensetracker.Targets;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.R;
import com.software.dk198.expensetracker.SpendingCategories.SpendingCategory;
import com.software.dk198.expensetracker.Targets.Target;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;



public class PieChartShowingFragment extends Fragment{
    DBHelper database;
    PieChart pieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_piechart, null);

        Bundle extras = getActivity().getIntent().getExtras();
        String target_name = extras.getString("targetName");
        int target_id = extras.getInt("targetId");
        database = new DBHelper(view.getContext());
        Target target = database.getTargetById(target_id);
        ArrayList<SpendingCategory> categories = database.getDifferentCategoriesOfTarget(target);

        pieChart = (PieChart) view.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        // Create and will the values for x and y
        ArrayList<Entry> y_vals = new ArrayList<>();
        ArrayList<String> x_vals = new ArrayList<>();   // names of categories
        for(SpendingCategory category : categories) {
            if (category.getSpent_in_category() > 0) {
                y_vals.add(new Entry(category.getSpent_in_category(), category.getCategory_id()));
                x_vals.add(category.getCategory_name());
            }
        }
        PieDataSet dataSet = new PieDataSet(y_vals, "Categories");
        PieData data = new PieData(x_vals, dataSet);
        data.setValueFormatter(new DefaultValueFormatter(0));



        pieChart.setData(data);
        pieChart.setDescription("");

        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setHoleRadius(50f);

        String target_color = target.getPieChartColor();
        switch(target_color){
            case "Vordiplom":
                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                break;
            case "Joyful":
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                break;
            case "Colorful":
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                break;
            case "Liberty":
                dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
                break;
            case "Pastel":
                dataSet.setColors(ColorTemplate.PASTEL_COLORS);
                break;
        }

        data.setValueTextSize(20f);
        data.setValueTextColor(Color.DKGRAY);
//        pieChart.getLegend().setEnabled(false);
        // Legends
        Legend l = pieChart.getLegend();
        l.setTextSize(15f);
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
//        pieChart.setOnChartValueSelectedListener(this);
        pieChart.animateXY(1400, 1400);
        if(y_vals.size()>0)
            pieChart.setCenterText(target.getName());
        else
            pieChart.setCenterText("Empty");
        pieChart.setCenterTextSize(25f);
        // undo all highlights
        pieChart.highlightValues(null);
        // update pie chart
        pieChart.invalidate();
        return view;
    }
}