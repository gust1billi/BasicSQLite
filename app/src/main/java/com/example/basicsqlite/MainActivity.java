package com.example.basicsqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.basicsqlite.db.DatabaseHelper;
import com.example.basicsqlite.rv.Data;
import com.example.basicsqlite.rv.DataRVAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    Toolbar toolbar;
    FloatingActionButton addBtn;

    RecyclerView mainRV; DataRVAdapter adapter; LinearLayoutManager layoutManager;
//    GridLayoutManager layoutManager;

    DatabaseHelper myDB;
    List<Data> data;

    int pointer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getTitleCondensed().equals("hi") ) {
            Toast.makeText(MainActivity.this, "Hello World!" , Toast.LENGTH_SHORT).show();
        } else if (item.getTitleCondensed().equals("pop")){
            Toast.makeText(MainActivity.this, "POP!", Toast.LENGTH_SHORT).show();
        } else if (item.getTitleCondensed().equals("prune" ) ){
            for (int i = 0; i < pointer; i++) {
                data.remove( data.size() - 1 );
                adapter.notifyItemRemoved(data.size());
            }
            myDB.onUpgrade(myDB.getReadableDatabase(), 0, 0);
            checkData();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = findViewById(R.id.floatingAddBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InsertDataActivity.class);
                startActivity(intent);
            }
        });

//        getActionBar().hide();
//        toolbar = (Toolbar) findViewById(R.id.toolbar); setSupportActionBar(toolbar);

        myDB = new DatabaseHelper(MainActivity.this);

        mainRV = findViewById(R.id.mainRecyclerView);

        data = new ArrayList<>(); populateData(); checkData();
        adapter = new DataRVAdapter(MainActivity.this, data);
//        layoutManager = new GridLayoutManager(MainActivity.this, 2);
        layoutManager = new LinearLayoutManager(MainActivity.this);

        mainRV.setAdapter(adapter); mainRV.setLayoutManager(layoutManager);
    }

    private void populateData() {
        // Dummy Data
        data.add(new Data("Judul", "Isi", 1));
        data.add(new Data("Judul", "Isi", 2));
        data.add(new Data("Judul", "Isi", 3));
        data.add(new Data("Judul", "Isi", 4));
        data.add(new Data("Judul", "Isi", 5));
        data.add(new Data("Judul", "Isi", 6));
        data.add(new Data("Judul", "Isi", 7));
        data.add(new Data("Judul", "Isi", 8));
        data.add(new Data("Judul", "Isi", 9));
        data.add(new Data("Judul", "Isi", 10));
    }

    private void checkData(){
        Cursor cursor = myDB.readAllData(); // Reads all of SQLite data in 1 table

        Toast.makeText(MainActivity.this, "size: " + cursor.getCount(), Toast.LENGTH_SHORT).show();
        pointer = cursor.getCount();

        while (cursor.moveToNext()){
            data.add(new Data(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3)));
        }

//        if (cursor.getCount() == 0){
//            Toast.makeText(MainActivity.this, "Empty Data Storage", Toast.LENGTH_SHORT).show();
//        } else {
//            data.add(new Data(
//                    cursor.getString(0),
//                    cursor.getString(1),
//                    cursor.getString(2),
//                    cursor.getInt(3)));
//            adapter.notifyItemInserted(data.size()-1 );
//        }

        // WILL HAVE DUPLICATED DATA ERROR, TABLE IS ALWAYS READ ALL;
        // TEMP SOLUTION, CLEAR THEN RE ADD
    }



    //    private void backBtn(){
//        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//    }


    @Override
    protected void onResume() {
        super.onResume(); adapter.notifyDataSetChanged();
    }
}