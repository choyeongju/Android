package com.example.noteLib.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.databinding.ReadingroomItemInMainBinding;

import java.util.ArrayList;

public class MainReadingRoomAdapter extends RecyclerView.Adapter<MainReadingRoomViewHolder> {
    private ArrayList<String> items = null;

    public MainReadingRoomAdapter(ArrayList<String> list){
        items = list;
    }

    // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public MainReadingRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReadingroomItemInMainBinding readingroomItemInMainBinding = ReadingroomItemInMainBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MainReadingRoomViewHolder(readingroomItemInMainBinding);
    }


    public void onBindViewHolder(@NonNull MainReadingRoomViewHolder holder, int position) {
        if (items != null)
            holder.readingroomItemInMainBinding.readingRoomSample.setText(items.get(position));
    }

    public int getItemCount() {
        return items.size();
    }

    // 어댑터 아이템 수정
    public void setItems(ArrayList<String> list){
        items = list;
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
