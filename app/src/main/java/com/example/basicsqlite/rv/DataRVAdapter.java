package com.example.basicsqlite.rv;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    Boolean isSwitched = false;

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

        holder.id.setText( data.getId( ) );
        holder.desc.setText( data.getDesc( ) );
        holder.title.setText( data.getTitle( ) );
        holder.num.setText( String.valueOf( data.getNum( ) ) );

        if ( data.getImgUri() == null ){
            holder.img.setImageResource(R.drawable.default_image);
        } else holder.img.setImageURI( Uri.parse(data.getImgUri( ) ) );

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(ctx, InsertDataActivity.class);
            ((MainActivity)ctx).setUpdateGate(true);
            ((MainActivity) ctx).setPointer(position);
            ((MainActivity) ctx).printToast("Address: " + position);

            i.putExtra("key", ((MainActivity)ctx).getUpdateGate() );
            i.putExtra("title", holder.title.getText() );
            i.putExtra("data", holder.desc.getText() );
            i.putExtra("num", holder.num.getText() );
            i.putExtra("id", holder.id.getText() );
            i.putExtra("position", position );

            ((MainActivity)ctx).openNextActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return database.size();
    }

    public void popLastItem(int position) {
        notifyItemRemoved( position );
        notifyItemRangeRemoved( position, position );
    }

    public void setDataShown(List<Data> filteredList) {
        database = filteredList; notifyDataSetChanged();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView id, title, desc, num; ImageView img;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.rvDataId);
            img = itemView.findViewById(R.id.image_data);
            desc = itemView.findViewById(R.id.rvDataString);
            num = itemView.findViewById(R.id.rvValueString);
            title = itemView.findViewById(R.id.rvTitleValueString);
        }
    }

    public Boolean getSwitched() {
        return isSwitched;
    }

    public Boolean toggleViewSwitched() {
        isSwitched = !isSwitched; // FALSE -> TRUE & TRUE -> FALSE
        ((MainActivity)ctx).printToast("Switch: " + isSwitched);
        return isSwitched;
    }
}
