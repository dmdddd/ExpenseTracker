package com.software.dk198.expensetracker.DataBackup;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.software.dk198.expensetracker.R;
import java.io.File;
import java.util.ArrayList;


public class ChoosingBackupFileActivity extends AppCompatActivity {
    public static ArrayList<String> file_paths;
    public static BackupFileAdapter adapter;
    public static RecyclerView fileRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_backup_file);

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ExpenseTracker");
        File[] files = directory.listFiles();
        file_paths = new ArrayList<>();
        Log.d("Files", "Path: " + directory.getAbsolutePath());
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            this.file_paths.add(files[i].getAbsolutePath());
            Log.d("Files", "FileName:" + files[i].getAbsolutePath());
        }
        fileRecyclerView = (RecyclerView) findViewById(R.id.fileRecyclerView);
        adapter = new BackupFileAdapter(this.file_paths);
        fileRecyclerView.setAdapter(adapter);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
