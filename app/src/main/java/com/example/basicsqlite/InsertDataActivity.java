package com.example.basicsqlite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.basicsqlite.db.DatabaseHelper;

public class InsertDataActivity extends AppCompatActivity {

    EditText editTitle, editData, editNumber;
    ImageView deleteBtn;
    Button actionBtn;

    boolean key = false; String id; int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); setSupportActionBar(toolbar);

        editTitle = findViewById(R.id.addTitle);
        editData = findViewById(R.id.addData);
        editNumber = findViewById(R.id.addNumber);
        actionBtn = findViewById(R.id.actionButton);
        deleteBtn = findViewById(R.id.deleteBtn); deleteBtn.setVisibility(View.INVISIBLE);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteDialog();
            }
        });

        whenUpdate();
        /** Checks if the Activity is called by FAB or by View Holder of Recycler View
         * Adds the data from the card clicked to the editText on this Layout
         * Changes the Button word from Add Data to Update Data
         * Checks the Key Value and gatekeeps the result
        * */

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper myDB = new DatabaseHelper(InsertDataActivity.this);
                if (editTitle.getText().length() == 0 || editData.getText().length() == 0
                        || editNumber.getText().length() == 0){
                    Toast.makeText(InsertDataActivity.this, "Input Data Required", Toast.LENGTH_SHORT).show();
                } else {
                    if (key){
                        key = false; myDB.updateData(
                                id,
                                editTitle.getText().toString(),
                                editData.getText().toString(),
                                Integer.parseInt( editNumber.getText().toString())
                        );
                    } else {
                        myDB.addData(
                                editTitle.getText().toString(),
                                editData.getText().toString(),
                                Integer.parseInt( editNumber.getText().toString() ) );
                    } // Add or Update Button ELIF
                    finish();
                } // ELIF
            } // ON CLICK
        }); // ACTION BUTTON ON-CLICK-LISTENER

    } // ON-CREATE INSERT-DATA-ACTIVITY

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

                    finish();
                } else Toast.makeText(InsertDataActivity.this, "Admin Data; Cannot delete", Toast.LENGTH_SHORT).show();

                actionBtn.setVisibility(View.INVISIBLE);
            }
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();
    } // DIALOG BOX INFO
}