package com.example.noteLib.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.databinding.ReadingroomTableInMainBinding;
public class TableViewHolder extends RecyclerView.ViewHolder {
    public ReadingroomTableInMainBinding tablebinding;
    TableViewHolder (ReadingroomTableInMainBinding tablebinding)
    {
        super(tablebinding.getRoot());
        this.tablebinding = tablebinding;
    }

}
