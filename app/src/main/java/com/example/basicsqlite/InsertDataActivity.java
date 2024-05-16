package com.example.basicsqlite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.basicsqlite.db.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class InsertDataActivity extends AppCompatActivity {

    EditText editTitle, editData, editNumber;
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); setSupportActionBar(toolbar);

        editTitle = findViewById(R.id.addTitle);
        editData = findViewById(R.id.addData);
        editNumber = findViewById(R.id.addNumber);
        addBtn = findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper myDB = new DatabaseHelper(InsertDataActivity.this);
                if (editTitle.getText().length() == 0 || editData.getText().length() == 0
                        || editNumber.getText().length() == 0){
                    Toast.makeText(InsertDataActivity.this, "Input Data Required", Toast.LENGTH_SHORT).show();
                } else {
                    myDB.addData(
                            editTitle.getText().toString(),
                            editData.getText().toString(),
                            Integer.parseInt( editNumber.getText().toString() ) );
                }

            }
        });

    }
}