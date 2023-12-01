package com.example.noteLib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityMyRoomBinding;

public class My_room extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyRoomBinding binding = ActivityMyRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goLibraryMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(My_room.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });

        binding.goMyRoomBookshelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(My_room.this, My_room_bookshelf.class));
            }
        });

        binding.goNewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(My_room.this, New_note.class));
            }
        });
    }
}