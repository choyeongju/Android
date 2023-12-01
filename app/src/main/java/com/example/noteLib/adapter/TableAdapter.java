package com.example.noteLib.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.databinding.ReadingroomTableInMainBinding;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableViewHolder> {

    private ArrayList<TableRow> items = null;

    public TableAdapter(ArrayList<TableRow> list){
         items = list;
    }
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReadingroomTableInMainBinding tablebinding = ReadingroomTableInMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TableViewHolder(tablebinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        TableRow tableRow = items.get(position);
        if (tableRow.getTitle1() == null)
            holder.tablebinding.tableSample1.setVisibility(View.GONE);
        else
            holder.tablebinding.tableSample1.setText(tableRow.getTitle1());
        if (tableRow.getTitle2() == null)
            holder.tablebinding.tableSample2.setVisibility(View.GONE);
        else
            holder.tablebinding.tableSample2.setText(tableRow.getTitle2());
        if (tableRow.getTitle3() == null)
            holder.tablebinding.tableSample3.setVisibility(View.GONE);
        else
            holder.tablebinding.tableSample3.setText(tableRow.getTitle3());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<TableRow> list){
        items = list;
        notifyDataSetChanged();
    }
}
