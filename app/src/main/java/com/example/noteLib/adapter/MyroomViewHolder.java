package com.example.noteLib.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.databinding.ReadingroomItemInMainBinding;

public class MyroomViewHolder extends RecyclerView.ViewHolder {

    public ReadingroomItemInMainBinding readingroomItemInMainBinding;

    MyroomViewHolder(ReadingroomItemInMainBinding readingroomItemInMainBinding)
    {
        super(readingroomItemInMainBinding.getRoot());
        this.readingroomItemInMainBinding = readingroomItemInMainBinding;
    }
}