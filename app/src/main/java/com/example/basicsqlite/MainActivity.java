package com.example.basicsqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.basicsqlite.rv.Data;
import com.example.basicsqlite.rv.DataRVAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton addBtn;

    RecyclerView mainRV; DataRVAdapter adapter; GridLayoutManager layoutManager;

    List<Data> data;

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

        mainRV = findViewById(R.id.mainRecyclerView);

        data = new ArrayList<>(); populateData();
        adapter = new DataRVAdapter(MainActivity.this, data);
        layoutManager = new GridLayoutManager(MainActivity.this, 2);

        mainRV.setAdapter(adapter); mainRV.setLayoutManager(layoutManager);
    }

    private void populateData() {
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

//    private void backBtn(){
//        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//    }

}