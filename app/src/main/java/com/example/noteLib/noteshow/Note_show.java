package com.example.noteLib.noteshow;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.noteLib.Login;
import com.example.noteLib.R;
import com.example.noteLib.databinding.ActivityNoteShowBinding;
import com.example.noteLib.quiet.CollectedNote;
import com.example.noteLib.quiet.Note;
import com.example.noteLib.quiet.User;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Note_show extends AppCompatActivity {

    private AlertDialog listDialog;
    private ActivityNoteShowBinding binding;
    private Note note;
    private boolean isSign;
    private File tempfile;
    private StorageReference gsReference;
    private User myuser;
    private String selectedBookshelf;
    private FirebaseFirestore db;

    private void showToast(String message){
        Toast toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }
    DialogInterface.OnClickListener dialogListener =new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            ArrayList<String> datas = myuser.getMybookshelves();
            if (dialog == listDialog && which == DialogInterface.BUTTON_NEGATIVE) {
                selectedBookshelf = null;
            }
            else if (dialog == listDialog && which == DialogInterface.BUTTON_POSITIVE) {
                showToast(selectedBookshelf + " 선택");
                downloadNote();
            }
            else {
                selectedBookshelf = datas.get(which);
                //datas[whtch] 이름의 bookshelf에 노트 경로(id) 저장
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeCloudFirestore();

        binding = ActivityNoteShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isSign = getIntent().getBooleanExtra("isSign", false);
        note = (Note)getIntent().getSerializableExtra("Note");
        myuser = (User)getIntent().getSerializableExtra("User");
        set_info();

        // Create a reference to a file from a Google Cloud Storage URI
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (note.getNoteUri() != null && (gsReference = storage.getReferenceFromUrl(note.getNoteUri())) != null)   //pdf 파일이 있다면..
            set_pdfViewer();
        else { //pdf가 없으면..
            binding.progressBarInNoteShow.setVisibility(View.GONE);
            binding.progressText.setVisibility(View.GONE);
        }

        getSupportFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if(result.getBoolean("bundleKey"))
                    binding.bottomBar.setVisibility(View.VISIBLE);
            }
        });

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        User myuser = null;
                        isSign = o.getData().getBooleanExtra("isSign", false);
                        if(o.getResultCode() == RESULT_OK) {
                            myuser = (User)o.getData().getSerializableExtra("User");
                        }
                        if (isSign && myuser.getNickname().equals(note.getUserNickname())) { //닉네임 일치 시
                            startActivity(new Intent(Note_show.this, Note_show_for_me.class)
                                    .putExtra("isSign", isSign)
                                    .putExtra("Note", note)
                                    .putExtra("User", myuser));
                            finish();
                        }
                    }
                });

        binding.btnSaveInMybookshelf.setOnClickListener(v -> {

            if (isSign) {
                ArrayList<String> data = myuser.getMybookshelves();
                selectedBookshelf = data.get(0);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("노트를 저장할 책장을 선택하세요");
                builder.setSingleChoiceItems(data.toArray(new String[data.size()]), 0, dialogListener);
                builder.setNegativeButton("취소", dialogListener);
                builder.setPositiveButton("확인", dialogListener);
                listDialog = builder.create();
                listDialog.show();
            }
            else{
                launcher.launch(new Intent(Note_show.this, Login.class));
            }
        });

        binding.noteShowBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gsReference != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("note", note);
                    Fragment fragment = new DownloadFragment();
                    fragment.setArguments(bundle);
                    binding.bottomBar.setVisibility(View.GONE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.note_show, fragment)
                            .commit();
                }
                else
                    showToast("pdf 파일이 존재하지 않습니다.");
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String bookshelf;
        if (keyCode == KeyEvent.KEYCODE_BACK && (bookshelf = getIntent().getStringExtra("MyRoomFragment")) != null){
            Intent intent = new Intent().putExtra("isSign", isSign)
                            .putExtra("MyRoomFragment", bookshelf)
                            .putExtra("User", myuser);
            setResult(RESULT_OK, intent);
            finish();
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent().putExtra("isSign", isSign);
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    private void set_info() {
        ArrayList<TextView> idList = new ArrayList<TextView>();
        idList.add(findViewById(R.id.note_show_tag1));
        idList.add(findViewById(R.id.note_show_tag2));
        idList.add(findViewById(R.id.note_show_tag3));
        idList.add(findViewById(R.id.note_show_tag4));
        idList.add(findViewById(R.id.note_show_tag5));
        binding.noteShowNoteTitle.setText(note.getNoteTitle());
        binding.noteShowUploadDate.setText(note.getUploadDate());
        binding.noteShowNickname.setText(note.getUserNickname());
        binding.noteShowExplainNote.setText(note.getExplainNote());
        ArrayList<String> tagList = note.getTagList();
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

    private void set_pdfViewer() {
        File destinationPath = getCacheDir();
        try {
            tempfile = File.createTempFile("pdf", ".pdf", destinationPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gsReference.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("SSU", "onSuccess: 미리 보기용 pdf 다운 완료");
                if (tempfile != null)
                    binding.pdfView.fromFile(tempfile)
                            .pages(0, 1, 2)
                            .enableSwipe(true) // allows to block changing pages using swipe
                            .swipeHorizontal(false)
                            .enableAnnotationRendering(true)
                            .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                            .scrollHandle(new DefaultScrollHandle(Note_show.this, true))
                            .onLoad(new OnLoadCompleteListener() {
                                @Override
                                public void loadComplete(int nbPages) {
                                    Log.d("SSU", "pdf viewer load complete");
                                    binding.progressBarInNoteShow.setVisibility(View.GONE);
                                    binding.progressText.setVisibility(View.GONE);
                                }
                            })
                            .load();
                else
                    Log.d("SSU", "onSuccess: 파일이 비어있음");
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {

            }
        });
    }

    private void downloadNote() {
        CollectedNote collectedNote = new CollectedNote(note.getNoteTitle(), note.getNameOfReadingroom(), selectedBookshelf);
        db.collection("User").document(myuser.getUid())
                .collection("Mynotes").document(collectedNote.getId())
                .set(collectedNote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast(note.getNoteTitle() + "를 " + selectedBookshelf + "에 저장완료");
                    }
                });
    }
    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();  //db 객체 얻기
    }
}