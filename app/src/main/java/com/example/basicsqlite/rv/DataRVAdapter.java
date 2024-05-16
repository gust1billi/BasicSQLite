package com.example.basicsqlite.rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
