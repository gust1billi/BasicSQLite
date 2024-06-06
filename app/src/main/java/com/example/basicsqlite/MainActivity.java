package com.example.basicsqlite;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.basicsqlite.db.DatabaseHelper;
import com.example.basicsqlite.rv.Data;
import com.example.basicsqlite.rv.DataRVAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addBtn;

    RecyclerView mainRV; DataRVAdapter adapter; GridLayoutManager layoutManager;
    SearchView searchView;

    DatabaseHelper myDB;
    List<Data> data;

    int pointer; int rvLayoutType = 0;
    boolean addGate = false; boolean updateGate = false;

    ActivityResultLauncher<Intent> nextActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    checkData();

                    if( updateGate ){ // CALLED FROM ADAPTER: UPDATE GATE & POINTER
                        setUpdateGate(false);

                        if (result.getResultCode() == RESULT_OK ){
                            adapter.notifyItemChanged( pointer );
                        } else adapter.popItemPosition(pointer, data.size());

                    } else if (addGate){
                        addGate = false; adapter.notifyItemInserted(data.size() - 1 );
                    } // END OF IF GATE
                } // ON ACTIVITY RESULT
            } // END OF ACTIVITY RESULT CALLBACK < ACTIVITY RESULT >
    ); // ACTIVITY RESULT LAUNCHER : REGISTER FOR ACTIVITY RESULT

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

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
        int id = item.getItemId();

        if ( id == R.id.menu_hello ) {                      printToast("Hello World!");
        } else if (item.getTitleCondensed().equals("pop")){ check4Deletion();
        } else if (id == R.id.menu_vLinear_layout){ switchLayout(0, 1, true);
        } else if (id == R.id.menu_hLinear_layout){ switchLayout(1, 1, false);
        } else if (id == R.id.menu_grid_layout){    switchLayout(2, 2, true);
        } // END OF IF STATEMENT IN OPTIONS MENU
        /* Switch Layout is a function that is made because the functions of the 3 if statements
        * Type is the mode of the desired layout. It will be checked by the current layout
        * Span is the size of the layout. Grid layout wants the layout span to be in pairs
        * Orientation is the method of scrolling. True is Vertical, False is Horizontal
        * */
        cursor.close(); return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = findViewById(R.id.floatingAddBtn);
        addBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, InsertDataActivity.class);
            intent.putExtra("key", updateGate); addGate = true;

            openNextActivity(intent);
        });

        myDB = new DatabaseHelper(MainActivity.this);
        mainRV = findViewById(R.id.mainRecyclerView); data = new ArrayList<>();
        checkData();

        adapter = new DataRVAdapter(MainActivity.this, data);
        layoutManager = new GridLayoutManager(MainActivity.this, 1);

        mainRV.setAdapter(adapter); mainRV.setLayoutManager(layoutManager);
    } // ON CREATE

    private void checkData(){
        Cursor cursor = myDB.readAllData(); // Reads all of SQLite data in 1 table
        data.clear();

        if ( cursor.getColumnCount() == 4 ) myDB.alterTable();
        // PREPARES THE LOCATION FOR IMG TO BE SAVED IN SQLITE

        while (cursor.moveToNext( ) ) {
            if (cursor.getString(4) == null) {
                // Log.e("SQLITE IMG: ", "is Empty");
                data.add(new Data(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3)));
            } else {
                // Log.e("SQLITE IMG", cursor.getString(4));
                data.add(new Data(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4) ) );
            }

        } // TEMP SOLUTION, CLEAR THEN RE ADD. Any method to make this less time consuming?
    } // END OF CHECK DATA FUNCTION. USED TO PUT ORIGINAL DATA FROM DB TO RECYCLER VIEW

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

    private void switchLayout(int type, int span, boolean orientation){
        if (rvLayoutType == type){

            switch (rvLayoutType) {
                case 0:
                    printToast("View Type is already Vertical");
                    break;
                case 1:
                    printToast("View Type is already Horizontal");
                    break;
                case 2:
                    printToast("View Type is already Grid");
                    break;
            }

        } else {
            layoutManager.setSpanCount(span); rvLayoutType = type;

            if (!orientation){
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            } else layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        }
    }

    private void check4Deletion() {
        int dataId = Integer.parseInt(data.get( data.size() - 1 ).getId());
        if ( dataId > 22){
            printToast("POP");
            myDB.popLastRow( myDB.getReadableDatabase() );

            data.remove( pointer - 1 );
            adapter.popItemPosition(pointer - 1, pointer);
        } else Toast.makeText(MainActivity.this,
                "Admin Data - Cannot Delete", Toast.LENGTH_SHORT).show();
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

    public void printToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}