package com.example.basicsqlite;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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

    FloatingActionButton addBtn;

    RecyclerView mainRV; DataRVAdapter adapter; LinearLayoutManager layoutManager;

    DatabaseHelper myDB;
    List<Data> data;

    int pointer;
    boolean addGate = false; boolean updateGate = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Cursor cursor = myDB.readAllData(); pointer = cursor.getCount();

        if ( item.getItemId() == R.id.menu_hello ) {
            Toast.makeText(MainActivity.this, "Hello World!" , Toast.LENGTH_SHORT).show();
        } else if (item.getTitleCondensed().equals("pop")){
            Toast.makeText(MainActivity.this, "POP!", Toast.LENGTH_SHORT).show();

            if (data.size()>12){
                myDB.popLastRow( myDB.getReadableDatabase() );
                checkData(); adapter.popLastItem(pointer);
            } else Toast.makeText(MainActivity.this,
                    "Admin Data - Cannot Delete", Toast.LENGTH_SHORT).show();
        }
//        else if (item.getTitleCondensed().equals("prune" ) ){
//            for (int i = 0; i < pointer; i++) {
//                data.remove( data.size() - 1 );
//                adapter.notifyItemRemoved(data.size());
//            }
//
//            myDB.onUpgrade(myDB.getReadableDatabase(), 0, 0);
//            for (int i = 0; i < pointer; i++) {
//                adapter.notifyItemRemoved(i);
//            }
//        }

        cursor.close();

        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> nextActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent resultData = result.getData();
                    assert resultData != null;

                    checkData();

                    if(updateGate){
                        setUpdateGate(false); adapter.notifyItemChanged(pointer);
//                      Toast.makeText(MainActivity.this, "Boop", Toast.LENGTH_SHORT).show();
                    } else if (addGate){
                        addGate = false; adapter.notifyItemInserted(data.size() - 1 );
                    } else if (resultData.getBooleanExtra("deleteGate", false) ){
                        adapter.notifyItemRemoved(
                                resultData.getIntExtra("position", 0) );
                    }
                    // else adapter.notifyDataSetChanged();
                }
            }
    );

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
                addGate = true;

                openNextActivity(intent);
            }
        });
        myDB = new DatabaseHelper(MainActivity.this);

        mainRV = findViewById(R.id.mainRecyclerView);  data = new ArrayList<>();

        adapter = new DataRVAdapter(MainActivity.this, data);
//        layoutManager = new GridLayoutManager(MainActivity.this, 2);
        layoutManager = new LinearLayoutManager(MainActivity.this);

        mainRV.setAdapter(adapter); mainRV.setLayoutManager(layoutManager);
    } // ON CREATE

    @Override
    protected void onResume() {
        super.onResume(); checkData();
    }

    public void openNextActivity(Intent intent){
        nextActivityLauncher.launch(intent);
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
//        Toast.makeText(MainActivity.this, "Boop", Toast.LENGTH_SHORT).show();

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

}