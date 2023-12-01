package com.example.noteLib.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.MainActivity;
import com.example.noteLib.R;
import com.example.noteLib.fragment.BookShelfFragment;
import com.example.noteLib.quiet.CollectedNote;
import com.example.noteLib.quiet.User;
import com.example.noteLib.databinding.BookshelfItemBinding;

import java.util.ArrayList;

public class BookshelfAdapter extends RecyclerView.Adapter<BookshelfViewHolder> {

    private ArrayList<CollectedNote>[] items = null;
    private ArrayList<String> bookshelfName;
    private Context context;
    private boolean isSign;
    private User myuser;

    public BookshelfAdapter(ArrayList<CollectedNote>[] list, ArrayList<String> bookshelfName, boolean isSign, User myuser){
        items = list;
        this.bookshelfName = bookshelfName;
        this.isSign = isSign;
        this.myuser = myuser;
    }

    // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public BookshelfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        BookshelfItemBinding bookshelfItemBinding = BookshelfItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BookshelfViewHolder(bookshelfItemBinding);
    }


    public void onBindViewHolder(@NonNull BookshelfViewHolder holder, int position) {
        if (items != null && items[position] != null){
            holder.bookshelfItemBinding.bookshelfName.setText(bookshelfName.get(position));
            holder.bookshelfItemBinding.notesInBookshelfRcv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            MyroomAdapter noteInBookShelfAdapter = new MyroomAdapter(items[position], bookshelfName.get(position));
            Log.d("SSU", "책장 이름 : " + bookshelfName.get(position) + ", 노트 갯수 : " + items[position].size());
            holder.bookshelfItemBinding.notesInBookshelfRcv.setAdapter(noteInBookShelfAdapter);

            holder.bookshelfItemBinding.goBookshelfButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                    BookShelfFragment bookShelfFragment = BookShelfFragment.newInstance(bookshelfName.get(position), items[position], isSign, myuser);
                    transaction.replace(R.id.fragment_container, bookShelfFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

        }
    }

    public int getItemCount() {
        return items.length;
    }

    // 어댑터 아이템 수정
    public void setItems(ArrayList<CollectedNote>[] list){
        items = list;
        notifyDataSetChanged();
    }

    public void setBookshelfName(ArrayList<String> bookshelfName){
        this.bookshelfName = bookshelfName;
        notifyDataSetChanged();
    }
}
