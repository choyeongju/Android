package com.example.noteLib.noteshow;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteLib.Login;
import com.example.noteLib.R;
import com.example.noteLib.databinding.ActivityNoteShowForMeBinding;
import com.example.noteLib.quiet.Note;

import java.util.ArrayList;

public class Note_show_for_me extends AppCompatActivity {

    private ActivityNoteShowForMeBinding binding;
    private Note note;
    private boolean isSign;

    private void showToast(String message){
        Toast toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoteShowForMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isSign = getIntent().getBooleanExtra("isSign", false);
        note = (Note)getIntent().getSerializableExtra("Note");
        set_info();

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == RESULT_OK)
                            isSign = true;
                    }
                });

        binding.noteShowForMeBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent().putExtra("isSign", isSign);
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    private void set_info()
    {
        ArrayList<TextView> idList = new ArrayList<TextView>();
        idList.add(findViewById(R.id.note_show_for_me_tag1));
        idList.add(findViewById(R.id.note_show_for_me_tag2));
        idList.add(findViewById(R.id.note_show_for_me_tag3));
        idList.add(findViewById(R.id.note_show_for_me_tag4));
        idList.add(findViewById(R.id.note_show_for_me_tag5));
        ArrayList<String> tagList = note.getTagList();
        binding.noteShowForMeTitle.setText(note.getNoteTitle());
        binding.noteShowForMeNickname.setText(note.getUserNickname());
        binding.noteShowForMeExplainNote.setText(note.getExplainNote());
        for (int i = 0; i < 5; i++)
        {
            if (i < tagList.size() && tagList.get(i) != null)
                idList.get(i).setText(tagList.get(i));
            else
                idList.get(i).setVisibility(View.GONE);
        }
    }
}