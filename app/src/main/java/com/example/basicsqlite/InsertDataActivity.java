package com.example.basicsqlite;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.basicsqlite.db.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InsertDataActivity extends AppCompatActivity {

    EditText editTitle, editData, editNumber;
    ImageView deleteBtn, img;
    Button actionBtn, uploadBtn;

    boolean key = false; String id; int position, imgMethod, getRequestCameraPermission;

    AlertDialog.Builder builder; AlertDialog dialog;

    Uri uri; String stringUri;

    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int REQUEST_GALLERY_PERMISSION = 0;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    ActivityResultLauncher<Intent> imgResultLauncher;

    private void registerImageResultLauncher() {
        imgResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if ( result.getResultCode() == RESULT_OK ){
                            assert result.getData() != null;

                            switch (imgMethod){
                                case 0 :
                                    try {
                                        uri = result.getData().getData();
                                        loadImage(uri);
                                    } catch (Exception e){
                                        e.getStackTrace(); printToast("No Image Selected");
                                    } break;
                                case 1 :
                                    Bundle extras = result.getData().getExtras();
                                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                                    try {
                                        uri = saveBitmapToGallery( imageBitmap );
                                        loadImage(uri);

                                        stringUri = String.valueOf(uri);

                                        printToast("Image Saved");
                                        Log.e("URI", stringUri);
                                    } catch (Exception e){
                                        e.getStackTrace(); printToast("Image not saved");
                                    } break;
                                case 2 :
                                    printToast("IMG FROM URL");
                                    break;
                            }
                        }
                    } // END OF IF STATEMENT THAT CHECKS IMG METHOD
                });
    } // CALLS WHEN THE USER OPENS GALLERY 4 IMG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);

        builder = new AlertDialog.Builder(InsertDataActivity.this);

        editTitle = findViewById(R.id.addTitle);
        editData = findViewById(R.id.addData);
        editNumber = findViewById(R.id.addNumber);

        img = findViewById(R.id.imagePreview);
        img.setOnClickListener(view -> {
            if ( uri == null ){
                Log.e("URI", "is Empty");
            } else  Log.e("URI", uri.toString());
        });

        actionBtn = findViewById(R.id.actionButton);

        uploadBtn = findViewById(R.id.uploadImg);
        uploadBtn.setOnClickListener(view -> {
            String[] choices = {"From Gallery", "From Camera" , "By URL"};
            imageChoiceDialog( choices );
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

                if ( uri == null ){
                    stringUri = null;
                } else stringUri = uri.toString();

                if (editTitle.getText().length() == 0 || editData.getText().length() == 0
                        || editNumber.getText().length() == 0){
                    printToast("Input Data Required");
                } else {
                    if (key){ // KEY TO MAKE SURE THE BUTTON UPDATES DB
                        myDB.updateData(
                                id,
                                editTitle.getText().toString(),
                                editData.getText().toString(),
                                Integer.parseInt( editNumber.getText().toString( ) ),
                                stringUri );
                    } else {
                        myDB.addData(
                                editTitle.getText().toString(),
                                editData.getText().toString(),
                                Integer.parseInt( editNumber.getText().toString() ),
                                stringUri );
                    } // Add or Update Button ELIF
                    setResult(RESULT_OK); finish();
                } // ELIF
            } // ON CLICK
        }); // ACTION BTN CLICK-LISTENER
    } // ON-CREATE INSERT-DATA-ACTIVITY

    private void imageChoiceDialog(String[] choices){
        builder.setTitle("Get Image Method!");
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        checkReadStoragePermAndGetImg();
                        break;
                    case 1:
                        checkCameraPermission();
                        break;
                    case 2:
                        printToast("BY URL");
                        break;
                }
            }
        }); dialog = builder.create(); dialog.show();
    } // END OF IMAGE OBTAINING METHOD DIALOG BOX

    private void checkCameraPermission() {
        imgMethod = REQUEST_CAMERA_PERMISSION;

        getRequestCameraPermission =
                ContextCompat.checkSelfPermission(
                        InsertDataActivity.this, Manifest.permission.CAMERA);

        if ( getRequestCameraPermission != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(InsertDataActivity.this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else openCamera();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try { imgResultLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("CAMERA NOT FOUND", e.getMessage( ) ); // display error state to the user
        } // END OF TRY CATCH. THIS IS USED IF THE DEVICE DOES NOT HAVE A CAMERA
    }

    private String getTimestampString() {
        long timestamp = System.currentTimeMillis();
        Date date = new Date(timestamp);

        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());

        return dateFormat.format(date);
    }

    private Uri saveBitmapToGallery( Bitmap imageBitmap ) throws IOException {
        String filename = getTimestampString();
        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File imageFile = new File(storageDir, filename + ".jpg");

        FileOutputStream fos = new FileOutputStream(imageFile);
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile( imageFile ) );
        sendBroadcast( mediaScanIntent );
//        Log.e("IMAGE FILE URI", String.valueOf(Uri.fromFile( imageFile ) ) );

        return Uri.fromFile( imageFile );
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
        imgMethod = REQUEST_GALLERY_PERMISSION;

        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");

        try { imgResultLauncher.launch(i);
        } catch (Exception e){
            Log.e("GET IMG ERROR", e.getMessage( ) );
            e.printStackTrace();
            printToast("Image Incompatible");
        }
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

            if ( getIntent().hasExtra("uri" ) ){
                loadImage( Uri.parse(extras.getString("uri" ) ) );
//                img.setImageURI( Uri.parse(extras.getString("uri" ) ) );
            } else img.setImageResource( R.drawable.default_image );

            position = extras.getInt("position");
            Log.e("POSITION", String.valueOf(position));

            actionBtn.setText(R.string.update);
        }
    } // WHEN THE USER ENTERS THE EDIT SPACE TO UPDATE

    private void confirmDeleteDialog(){
        builder.setTitle("Delete " + editTitle.getText() + "?");
        builder.setTitle("Are you sure that you want to Delete" + editTitle.getText() + "?")
                .setNegativeButton("No", (dialogInterface, i) -> { });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Integer.parseInt(id) > 22){
                    DatabaseHelper myDB = new DatabaseHelper(InsertDataActivity.this);
                    myDB.deleteOneRow(id);

                    Intent intent =
                            new Intent(InsertDataActivity.this, MainActivity.class);

                    setResult(RESULT_OK, intent); finish();
                } else printToast("Admin Data; Cannot delete");

                actionBtn.setVisibility(View.INVISIBLE);
            }
        });

        dialog = builder.create();
        dialog.show();
    } // END OF DELETE DIALOG BOX

    private void printToast(String bread) {
        Toast.makeText(InsertDataActivity.this, bread, Toast.LENGTH_SHORT).show();
    }

    private void loadImage(Uri imageUri){
        Glide.with(InsertDataActivity.this)
                .load(imageUri)
                .placeholder(R.drawable.default_image)
                .into(img);
    }
}