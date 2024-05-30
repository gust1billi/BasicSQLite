package com.example.basicsqlite;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.basicsqlite.db.DatabaseHelper;

public class InsertDataActivity extends AppCompatActivity {

    EditText editTitle, editData, editNumber;
    ImageView deleteBtn, img;
    Button actionBtn, uploadBtn;

    boolean key = false; String id; int position;

    Uri uri;

    private static final int STORAGE_PERMISSION_CODE = 100;

    ActivityResultLauncher<Intent> imgResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);

        editTitle = findViewById(R.id.addTitle);
        editData = findViewById(R.id.addData);
        editNumber = findViewById(R.id.addNumber);
        img = findViewById(R.id.imagePreview);

        actionBtn = findViewById(R.id.actionButton);

        // TODO: CUSTOM DIALOG LOGIC GATE. CAMERA/GALLERY/URL
        uploadBtn = findViewById(R.id.uploadImg);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkReadStoragePermAndGetImg();
            }
        });

        deleteBtn = findViewById(R.id.deleteBtn); deleteBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setOnClickListener( view -> confirmDeleteDialog() );

        whenUpdate(); registerImageResultLauncher();
        /* Checks if the Activity is called by FAB or by View Holder of Recycler View
         * Adds the data from the card clicked to the editText on this Layout
         * Changes the Button word from Add Data to Update Data
         * Checks the Key Value and gate-keeps the result
        * */

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper myDB = new DatabaseHelper(InsertDataActivity.this);
                if (editTitle.getText().length() == 0 || editData.getText().length() == 0
                        || editNumber.getText().length() == 0){
                    printToast("Input Data Required");
                } else {
                    if (key){
                        key = false;

                        myDB.updateData(
                                id,
                                editTitle.getText().toString(),
                                editData.getText().toString(),
                                Integer.parseInt( editNumber.getText().toString( ) ),
                                null ); // NULL IMG Until Im sure how to put uri to string
                    } else {
                        myDB.addData(
                                editTitle.getText().toString(),
                                editData.getText().toString(),
                                Integer.parseInt( editNumber.getText().toString() ),
                                null );
                    } // Add or Update Button ELIF
                    finish();
                } // ELIF
            } // ON CLICK
        }); // ACTION BUTTON ON-CLICK-LISTENER

    } // ON-CREATE INSERT-DATA-ACTIVITY

    private void registerImageResultLauncher() {
        imgResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                try {
                    uri = result.getData().getData();
                    img.setImageURI(uri);
                }catch (Exception e){
                    e.getStackTrace();
                    printToast("No Image Selected");
                }
            }
        });
    }

    private void checkReadStoragePermAndGetImg() {
        // IF SDK is 30 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (!Environment.isExternalStorageManager() ){
                try {
                    Intent intent = new Intent (Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s",
                            getApplicationContext().getPackageName() ) ) );
                    startActivityIfNeeded(intent, 101);
                } catch (Exception e ){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivityIfNeeded(intent, 101);
                }
            } else pickImg();
        } else { // IF SDK 23-29
            if (ActivityCompat.checkSelfPermission(InsertDataActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(InsertDataActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else pickImg();
        }
    }

    private void pickImg() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        imgResultLauncher.launch(i);
    }

    // Adds the data from the card clicked to the editText on this Layout
    private void whenUpdate() {
        Bundle extras = getIntent().getExtras();
        if (extras.getBoolean("key") ){ key = true;
            deleteBtn.setVisibility(View.VISIBLE);

            editNumber.setText( extras.getString("num") );
            editTitle.setText(extras.getString("title"));
            editData.setText(extras.getString("data"));
            id = extras.getString("id");
            position = extras.getInt("position");

            actionBtn.setText(R.string.update);
        }
    } // WHEN UPDATES

    private void confirmDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(InsertDataActivity.this);
        builder.setTitle("Delete " + editTitle.getText() + "?");
        builder.setTitle("Are you sure that you want to Delete" + editTitle.getText() + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Integer.parseInt(id) > 22){
                    DatabaseHelper myDB = new DatabaseHelper(InsertDataActivity.this);
                    myDB.deleteOneRow(id);

                    Intent intent = new Intent(InsertDataActivity.this, MainActivity.class);
                    intent.putExtra("deleteGate", true);
                    intent.putExtra("position", position);

                } else printToast("Admin Data; Cannot delete");

                finish(); // actionBtn.setVisibility(View.INVISIBLE);
            }
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();
    } // DIALOG BOX INFO

    private void printToast(String bread) {
        Toast.makeText(InsertDataActivity.this, bread, Toast.LENGTH_SHORT).show();
    }
}