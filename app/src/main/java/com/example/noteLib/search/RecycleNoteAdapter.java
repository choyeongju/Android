package com.example.noteLib.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.databinding.NoteItemInSearchBinding;
import com.example.noteLib.quiet.Note;

import java.util.ArrayList;

public class RecycleNoteAdapter extends RecyclerView.Adapter<RecycleNoteViewHolder> {
    private ArrayList<Note> items = null;
    private Context context;

    public RecycleNoteAdapter(ArrayList<Note> list, Context context){
        items = list;
        this.context = context;
    }

    // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public RecycleNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
//        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View view = inflater.inflate(R.layout.note_item_in_search, parent, false);
//        RecycleNoteAdapter.ViewHolder vh = new RecycleNoteAdapter.ViewHolder(view);
        NoteItemInSearchBinding binding = NoteItemInSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecycleNoteViewHolder(binding);
    }


    public void onBindViewHolder(@NonNull RecycleNoteViewHolder holder, int position) {
        String noteTitle = items.get(position).getNoteTitle();
        String nickname = items.get(position).getUserNickname();

        holder.binding.noteSampleTitle.setText(noteTitle);
        holder.binding.noteSampleNickname.setText(nickname);

        ArrayList<TextView> idList = new ArrayList<TextView>();
        idList.add(holder.binding.noteSampleTag1);
        idList.add(holder.binding.noteSampleTag2);
        idList.add(holder.binding.noteSampleTag3);
        idList.add(holder.binding.noteSampleTag4);
        idList.add(holder.binding.noteSampleTag5);
        ArrayList<String> tagList = items.get(position).getTagList();
        if (tagList == null)
            return ;
        for (int i = 0; i < 5; i++)
        {
            if (i < tagList.size() && tagList.get(i) != null) {
                idList.get(i).setText(tagList.get(i));
                idList.get(i).setVisibility(View.VISIBLE);
            }
            else
                idList.get(i).setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        return items.size();
    }

    // 어댑터 아이템 수정
    public void setItems(ArrayList<Note> list){
        items = list;
        notifyDataSetChanged();
    }
}