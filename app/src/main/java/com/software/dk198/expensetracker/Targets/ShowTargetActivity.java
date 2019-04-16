package com.software.dk198.expensetracker.Targets;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;


import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.MainActivity;
import com.software.dk198.expensetracker.R;

import java.util.Locale;

public class ShowTargetActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView mTextMessage;
    private static final String TAG = "ShowTargetActivity";
    int target_id;
    public BottomNavigationView navigation;
    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity Loaded");

        // Languages
        Locale locale = new Locale(MainActivity.language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= 17) { config.setLocale(locale); } else { config.locale = locale; }
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_show_target);

//        setContentView(R.layout.activity_show_target);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        database = new DBHelper(this);
        Bundle extras = getIntent().getExtras();
        target_id = extras.getInt("targetId");

        loadFragment(new CategoriesShowingFragment());
    }

    private  boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        else
            return false;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch(menuItem.getItemId()){
            case R.id.navigation_home:
                fragment = new CategoriesShowingFragment();
                break;

            case R.id.navigation_piechart:
                fragment = new PieChartShowingFragment();
                break;

            case R.id.navigation_target_settings:
                fragment = new TargetSettingsFragment();
                break;

        }
        return loadFragment(fragment);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("SelectedItemId", navigation.getSelectedItemId());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedItemId = savedInstanceState.getInt("SelectedItemId");
        navigation.setSelectedItemId(selectedItemId);
    }

}
