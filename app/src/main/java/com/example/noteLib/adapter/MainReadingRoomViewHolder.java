package com.example.noteLib.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.databinding.ReadingroomItemInMainBinding;


public class MainReadingRoomViewHolder extends RecyclerView.ViewHolder {
    public ReadingroomItemInMainBinding readingroomItemInMainBinding;

    MainReadingRoomViewHolder(ReadingroomItemInMainBinding readingroomItemInMainBinding)
    {
        super(readingroomItemInMainBinding.getRoot());
        this.readingroomItemInMainBinding = readingroomItemInMainBinding;
    }
}
