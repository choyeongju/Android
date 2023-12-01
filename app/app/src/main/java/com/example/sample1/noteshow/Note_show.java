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
import com.example.noteLib.databinding.ActivityNoteShowBinding;
import com.example.noteLib.quiet.Note;

import java.util.ArrayList;

public class Note_show extends AppCompatActivity {

    private AlertDialog listDialog;
    private ActivityNoteShowBinding binding;
    private Note note;
    private boolean isSign;

    private void showToast(String message){
        Toast toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }
    DialogInterface.OnClickListener dialogListener =new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(dialog==listDialog){
                String[] datas=getResources().getStringArray(R.array.noteshow_dialog_array);
                showToast(datas[which]+" 선택");
                //datas[whtch] 이름의 bookshelf에 노트 경로(id) 저장
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoteShowBinding.inflate(getLayoutInflater());
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

        binding.btnSaveInMybookshelf.setOnClickListener(v -> {

            if (isSign) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("노트를 저장할 책장을 선택하세요");
                builder.setSingleChoiceItems(R.array.noteshow_dialog_array, 0, dialogListener);
                builder.setNegativeButton("확인", null);
                builder.setPositiveButton("취소", null);
                listDialog = builder.create();
                listDialog.show();
            }
            else{
                launcher.launch(new Intent(Note_show.this, Login.class));
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
        idList.add(findViewById(R.id.note_show_tag1));
        idList.add(findViewById(R.id.note_show_tag2));
        idList.add(findViewById(R.id.note_show_tag3));
        idList.add(findViewById(R.id.note_show_tag4));
        idList.add(findViewById(R.id.note_show_tag5));
        ArrayList<String> tagList = note.getTagList();
        binding.noteShowNoteTitle.setText(note.getNoteTitle());
        binding.noteShowNickname.setText(note.getUserNickname());
        binding.noteShowExplainNote.setText(note.getExplainNote());
        for (int i = 0; i < 5; i++)
        {
            if (i < tagList.size() && tagList.get(i) != null)
                idList.get(i).setText(tagList.get(i));
            else
                idList.get(i).setVisibility(View.GONE);
        }
    }
}