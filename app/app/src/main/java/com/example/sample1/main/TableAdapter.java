package com.example.noteLib.main;

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

    // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReadingroomTableInMainBinding tablebinding = ReadingroomTableInMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TableViewHolder(tablebinding);
    }


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

    public int getItemCount() {
        return items.size();
    }

    // 어댑터 아이템 수정
    public void setItems(ArrayList<TableRow> list){
        items = list;
        notifyDataSetChanged();
    }
}
