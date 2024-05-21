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

    int pointer; boolean updateGate = false; boolean key = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Cursor cursor = myDB.readAllData(); pointer = cursor.getCount();

        if (item.getTitleCondensed().equals("hi") ) {
            Toast.makeText(MainActivity.this, "Hello World!" , Toast.LENGTH_SHORT).show();
        } else if (item.getTitleCondensed().equals("pop")){
            Toast.makeText(MainActivity.this, "POP!", Toast.LENGTH_SHORT).show();

            myDB.popLastRow( myDB.getReadableDatabase() , adapter, pointer );

//            adapter.notifyItemRemoved( pointer );
//            adapter.notifyItemRangeRemoved(pointer, cursor.getCount( ) );

        } else if (item.getTitleCondensed().equals("prune" ) ){
            for (int i = 0; i < pointer; i++) {
                data.remove( data.size() - 1 );
                adapter.notifyItemRemoved(data.size());
            }

            myDB.onUpgrade(myDB.getReadableDatabase(), 0, 0);
            for (int i = 0; i < pointer; i++) {
                adapter.notifyItemRemoved(i);
            }
        }

        cursor.close();

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
                intent.putExtra("key", updateGate);
                nextActivity(intent);
            }
        });

//        getActionBar().hide();
//        toolbar = (Toolbar) findViewById(R.id.toolbar); setSupportActionBar(toolbar);

        myDB = new DatabaseHelper(MainActivity.this);

        mainRV = findViewById(R.id.mainRecyclerView);

        data = new ArrayList<>();

        adapter = new DataRVAdapter(MainActivity.this, data);
//        layoutManager = new GridLayoutManager(MainActivity.this, 2);
        layoutManager = new LinearLayoutManager(MainActivity.this);

        mainRV.setAdapter(adapter); mainRV.setLayoutManager(layoutManager);
    } // ON CREATE

    @Override
    protected void onResume() {
        super.onResume(); checkData();

        if(updateGate){
            setUpdateGate(false); adapter.notifyItemChanged(pointer);
//            Toast.makeText(MainActivity.this, "Boop", Toast.LENGTH_SHORT).show();
        } else adapter.notifyDataSetChanged();
    } // ON RESUME

    public void nextActivity(Intent intent) {
        startActivity(intent);
    }

    public void setUpdateGate(boolean key){
        updateGate = key;
    }

    public boolean getUpdateGate(){
        return updateGate;
    }

    public void setPointer(int position){
        pointer = position;
    }

    private void checkData(){
        Cursor cursor = myDB.readAllData(); // Reads all of SQLite data in 1 table
        pointer = cursor.getCount(); data.clear();
//        Toast.makeText(MainActivity.this, "size: " + cursor.getCount(), Toast.LENGTH_SHORT).show();
        Toast.makeText(MainActivity.this, "Boop", Toast.LENGTH_SHORT).show();

        while (cursor.moveToNext()){
            data.add(new Data(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3)));
        }

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
}