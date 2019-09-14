package com.software.dk198.expensetracker.DataBackup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BackupActivity extends AppCompatActivity {
    private static final String TAG = "BackupActivity";
    DBHelper database;

    // GUI elements
    private Button importBtn;
    private Button exportBtn;
    private Button exportShareBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        // Initializing the buttons
        database = new DBHelper(this);
        final Context context = this;
        importBtn = (Button) findViewById(R.id.importBtn);
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Import Button");
                if (ContextCompat.checkSelfPermission(BackupActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // No writing permissions given yet
                    ActivityCompat.requestPermissions(BackupActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            11);

                } else {
                    Intent intent = new Intent(BackupActivity.this, ChoosingBackupFileActivity.class);
                    startActivity(intent);
                }
            }
        });
        exportBtn = (Button) findViewById(R.id.exportBtn);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Export Button");
                if (ContextCompat.checkSelfPermission(BackupActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Database export: No writing permission");
                    // No writing permissions given yet
                    ActivityCompat.requestPermissions(BackupActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            10);

                } else {
                    Log.d(TAG, "Database export: Permission granted");
                    exportDatabase(context, false);
                }
            }
        });

        exportShareBtn = (Button) findViewById(R.id.exportShareBtn);
        exportShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Export and Share Button");
                if (ContextCompat.checkSelfPermission(BackupActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // No writing permissions given yet
                    ActivityCompat.requestPermissions(BackupActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12);

                } else {
                    exportDatabase(context, true);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    exportDatabase(BackupActivity.this, false);
//                    Toast.makeText(BackupActivity.this, "Granted!", Toast.LENGTH_LONG).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(BackupActivity.this, "Permission denied, can not read files", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 12: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    exportDatabase(BackupActivity.this, true);
//                    Toast.makeText(BackupActivity.this, "Granted!", Toast.LENGTH_LONG).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(BackupActivity.this, "Permission denied, can not read files", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(BackupActivity.this, ChoosingBackupFileActivity.class);
                    startActivity(intent);
//                    Toast.makeText(BackupActivity.this, "Granted!", Toast.LENGTH_LONG).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(BackupActivity.this, "Permission denied, can not read files", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    void exportDatabase(Context context_, Boolean share_){
        Log.d(TAG, "Inside exportDatabase");

        database.checkpoint();

        final Context context = context_;
        final Boolean share = share_;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose file name");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected;
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy_MM_dd");
        String default_file_name = "db_" + mdformat.format(calendar.getTime());
        input.setText(default_file_name);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String file_name = input.getText().toString();
                if (file_name.length() > 0) {
                    file_name += ".db";
                    Log.d(TAG, "Database name has been chosen: " + file_name);

                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        Log.d(TAG, "Exporting allowed");
                        File myDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ExpenseTracker");
                        myDirectory.mkdirs();
                        File db_file = new File(myDirectory, file_name);

                        try {
                            // Creating a new file for the export and writing the current database's data there
                            db_file.createNewFile();
                            try {
                                String DB_NAME = database.DATABASE_NAME;
                                String currentDatabasePath = getDatabasePath(DB_NAME).getAbsolutePath();
                                File currentDB = new File(currentDatabasePath);
                                if (currentDB.isFile()){
                                    Log.d(TAG, "Current DB file found: " + currentDatabasePath);
                                }
                                if (currentDB.canRead()){
                                    Log.d(TAG, "Current DB file Can READ ");
                                }
                                // Open your local db as the input stream
                                InputStream myInput = new FileInputStream(currentDatabasePath);

                                // Open the empty db as the output stream
                                OutputStream myOutput = new FileOutputStream(db_file);

                                // transfer bytes from the input file to the output file
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = myInput.read(buffer)) > 0) {
                                    myOutput.write(buffer, 0, length);
                                }

                                // Close the streams
                                myOutput.flush();
                                myOutput.close();
                                myInput.close();

                                if (share){
                                    Log.d(TAG, "Sharing over email");
//                                    Uri path = Uri.fromFile(db_file);
                                    Uri path = getUriForFile(context, db_file);
                                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                    // set the type to 'email'
                                    emailIntent.setType("vnd.android.cursor.dir/email");
                                    String to[] = {""};
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                                    // File attachment
                                    emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                                    // Email subject
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Expense Tracker - Database Backup");
                                    // Email body
                                    SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                    String dateString = mdformat.format((Calendar.getInstance()).getTime());
                                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                            "Database backup, created on: " + dateString + ".");
                                    // Sending the email
                                    startActivity(Intent.createChooser(emailIntent , "Send email..."));
                                }
                                Toast.makeText(context, "Backup completed", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Log.e("Exception", "File write failed: " + e.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else
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

    private static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                String packageId = context.getPackageName();
                return FileProvider.getUriForFile(context, packageId, file.getAbsoluteFile());
            } catch (IllegalArgumentException e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    throw new SecurityException();
                } else {
                    return Uri.fromFile(file);
                }
            }
        } else {
            return Uri.fromFile(file);
        }
    }


}