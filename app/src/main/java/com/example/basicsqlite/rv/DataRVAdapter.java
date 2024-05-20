package com.example.basicsqlite.rv;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basicsqlite.InsertDataActivity;
import com.example.basicsqlite.MainActivity;
import com.example.basicsqlite.R;

import java.util.List;

public class DataRVAdapter extends RecyclerView.Adapter<DataRVAdapter.DataViewHolder> {
    Context ctx;

    public DataRVAdapter(Context ctx, List<Data> database) {
        this.ctx = ctx;
        this.database = database;
    }

    List<Data> database;

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_rv, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        Data data = database.get(position);

        holder.title.setText( data.getTitle( ) );
        holder.desc.setText( data.getDesc( ) );
        holder.num.setText( String.valueOf( data.getNum( ) ) );

        if (holder.id.getText( ) != null){
            holder.id.setText( data.getId( ) );
        } else holder.id.setText("");

        holder.itemView.setOnClickListener(view -> {
            Toast.makeText(ctx, "Num: " + holder.num.getText(), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ctx, InsertDataActivity.class);
            ((MainActivity)ctx).setUpdateGate(true);
            ((MainActivity) ctx).setPointer(position);

            i.putExtra("key", ((MainActivity)ctx).getUpdateGate() );
            i.putExtra("title", holder.title.getText() );
            i.putExtra("data", holder.desc.getText() );
            i.putExtra("num", holder.num.getText() );
            i.putExtra("id", holder.id.getText() );
            i.putExtra("position", position );

            ((MainActivity)ctx).nextActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return database.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView id, title, desc, num;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.rvDataId);
            title = itemView.findViewById(R.id.rvTitleValueString);
            desc = itemView.findViewById(R.id.rvDataString);
            num = itemView.findViewById(R.id.rvValueString);
        }
    }
}
