package com.example.noteLib.search;

import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.databinding.NoteItemInSearchBinding;

public class RecycleNoteViewHolder extends RecyclerView.ViewHolder {
    public NoteItemInSearchBinding binding;

    RecycleNoteViewHolder(NoteItemInSearchBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
