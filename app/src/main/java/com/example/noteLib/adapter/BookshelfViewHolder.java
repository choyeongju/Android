package com.example.noteLib.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.databinding.BookshelfItemBinding;

public class BookshelfViewHolder extends RecyclerView.ViewHolder {

    public BookshelfItemBinding bookshelfItemBinding;

    BookshelfViewHolder(BookshelfItemBinding bookshelfItemBinding)
    {
        super(bookshelfItemBinding.getRoot());
        this.bookshelfItemBinding = bookshelfItemBinding;
    }
}