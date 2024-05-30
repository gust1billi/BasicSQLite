package com.example.basicsqlite;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
    SearchView searchView;

    DatabaseHelper myDB;
    List<Data> data;

    int pointer;
    boolean addGate = false; boolean updateGate = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        // MenuItem actionSearch = menu.findItem(R.id.menu_search);

        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String filter) {
                if (filter.length() == 0){
                    originalData();
                } else filterData( filter );
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Cursor cursor = myDB.readAllData(); pointer = cursor.getCount();

        if ( item.getItemId() == R.id.menu_hello ) {
            Toast.makeText(MainActivity.this, "Hello World!" , Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this, "Size: " + cursor.getColumnCount() , Toast.LENGTH_SHORT).show();
        } else if (item.getTitleCondensed().equals("pop")){

            if (data.size()>12){
                Toast.makeText(MainActivity.this,
                        "POP!", Toast.LENGTH_SHORT).show();
                myDB.popLastRow( myDB.getReadableDatabase() );
                checkData(); adapter.popLastItem(pointer);
            } else Toast.makeText(MainActivity.this,
                    "Admin Data - Cannot Delete", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.menu_vlinear_layout){
            printToast("a");
        } else if (item.getItemId() == R.id.menu_hlinear_layout){
            printToast("B");
        } else if (item.getItemId() == R.id.menu_grid_layout){
            printToast("C");
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
        checkData();

        adapter = new DataRVAdapter(MainActivity.this, data);
//        layoutManager = new GridLayoutManager(MainActivity.this, 2);
        layoutManager = new LinearLayoutManager(MainActivity.this);

        mainRV.setAdapter(adapter); mainRV.setLayoutManager(layoutManager);
    } // ON CREATE

    private void filterData(String filter) {
        List<Data> filteredList = new ArrayList<>();
        for (Data filteredDataPosition : data) {
            if (filteredDataPosition.getTitle().toLowerCase().contains(filter)){
                filteredList.add(filteredDataPosition);
            } else if (filteredDataPosition.getDesc().toLowerCase().contains(filter)){
                filteredList.add(filteredDataPosition);
            }
        }

        adapter.setDataShown( filteredList );
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

    private void originalData() {
        adapter.setDataShown( data );
    }

    private void checkData(){
        Cursor cursor = myDB.readAllData(); // Reads all of SQLite data in 1 table
        pointer = cursor.getCount(); data.clear();
        if ( cursor.getColumnCount() == 4 ) myDB.alterTable();
        // PREPARES THE LOCATION FOR IMG TO BE SAVED IN SQLITE

        while (cursor.moveToNext( ) ) {
            data.add(new Data(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3)));
//            Log.e("SQLITE ID: ", cursor.getString(0)); // ID is always 1 BUG
        } // TEMP SOLUTION, CLEAR THEN RE ADD. Any method to make this less time consuming?
    } // END OF CHECK DATA FUNCTION. USED TO PUT ORIGINAL DATA FROM DB TO RECYCLER VIEW

    private void printToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}