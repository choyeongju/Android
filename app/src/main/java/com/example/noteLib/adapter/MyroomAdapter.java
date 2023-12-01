package com.example.noteLib.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.noteLib.quiet.CollectedNote;
import com.example.noteLib.databinding.ReadingroomItemInMainBinding;

import java.util.ArrayList;

public class MyroomAdapter extends RecyclerView.Adapter<MyroomViewHolder> {

    //1. 파이어베이스에서 로그인한 사용자의 노트 불러오기


    private ArrayList<CollectedNote> items = null;
    private String bookshelfName;

    public MyroomAdapter(ArrayList<CollectedNote> list, String bookshelfName){
        items = list;
        this.bookshelfName = bookshelfName;
    }

    // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public MyroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReadingroomItemInMainBinding readingroomItemInMainBinding = ReadingroomItemInMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyroomViewHolder(readingroomItemInMainBinding);
    }


    public void onBindViewHolder(@NonNull MyroomViewHolder holder, int position) {
        if (items != null && items.size() != 0) {
            if (items.get(position).getId().equals("sampleid")){
                holder.readingroomItemInMainBinding.readingRoomSample.setVisibility(View.GONE);
                holder.readingroomItemInMainBinding.readingRoomSample.setClickable(false);

            }
            else {
                holder.readingroomItemInMainBinding.readingRoomSample.setText(items.get(position).getId());
                Log.d("SSU", "set item : " + items.get(position).getId());
            }
        }
    }

    public int getItemCount() {
        return items.size();
    }

    // 어댑터 아이템 수정
    public void setItems(ArrayList<CollectedNote> list){
        items = list;
        notifyDataSetChanged();
    }

    public void setBookshelfName(String bookshelfName){
        this.bookshelfName = bookshelfName;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = items.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                items.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }
}
