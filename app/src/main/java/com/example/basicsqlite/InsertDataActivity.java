package com.example.basicsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.basicsqlite.db.DatabaseHelper;

public class InsertDataActivity extends AppCompatActivity {

    EditText editTitle, editData, editNumber;
    Button actionBtn;

    boolean key = false; String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); setSupportActionBar(toolbar);

        editTitle = findViewById(R.id.addTitle);
        editData = findViewById(R.id.addData);
        editNumber = findViewById(R.id.addNumber);
        actionBtn = findViewById(R.id.actionButton);

        whenUpdate(); // Adds the data from the card clicked to the editText on this Layout

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper myDB = new DatabaseHelper(InsertDataActivity.this);
                if (editTitle.getText().length() == 0 || editData.getText().length() == 0
                        || editNumber.getText().length() == 0){
                    Toast.makeText(InsertDataActivity.this, "Input Data Required", Toast.LENGTH_SHORT).show();
                } else {
                    if (key){
                        key = false;

                        myDB.updateData(
                                id,
                                editTitle.getText().toString(),
                                editData.getText().toString(),
                                Integer.parseInt( editNumber.getText().toString())
                        );

                    }else {
                        myDB.addData(
                                editTitle.getText().toString(),
                                editData.getText().toString(),
                                Integer.parseInt( editNumber.getText().toString() ) );
                    }
                }

            }
        });

    }

    // Adds the data from the card clicked to the editText on this Layout
    private void whenUpdate() {
        Bundle extras = getIntent().getExtras();
        if (extras.getBoolean("key") ){
            key = true;

            editNumber.setText( extras.getString("num") );
            editTitle.setText(extras.getString("title"));
            editData.setText(extras.getString("data"));
            id = extras.getString("id");

            actionBtn.setText(R.string.update);
        }
    }
}