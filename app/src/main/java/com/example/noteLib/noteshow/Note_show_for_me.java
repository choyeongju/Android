package com.example.noteLib.noteshow;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.noteLib.R;
import com.example.noteLib.databinding.ActivityNoteShowForMeBinding;
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

public class Note_show_for_me extends AppCompatActivity {

    private ActivityNoteShowForMeBinding binding;
    private Note note;
    private boolean isSign;
    private File tempfile;
    private StorageReference gsReference;
    private User myuser;
    private FirebaseFirestore db;

    private void showToast(String message){
        Toast toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeCloudFirestore();

        binding = ActivityNoteShowForMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isSign = getIntent().getBooleanExtra("isSign", false);
        note = (Note)getIntent().getSerializableExtra("Note");
        myuser = (User)getIntent().getSerializableExtra("User");
        set_info();

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
                        if(o.getResultCode() == RESULT_OK)
                            isSign = o.getData().getBooleanExtra("isSign", false);
                    }
                });


        binding.noteShowForMeBtnDownload.setOnClickListener(new View.OnClickListener() {
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
                            .add(R.id.note_show_for_me, fragment)
                            .commit();
                }
                else
                    showToast("pdf 파일이 존재하지 않습니다.");
            }
        });

        binding.noteShowForMeDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("ReadingRoom").document(note.getNameOfReadingroom()).collection("notes").document(note.getNoteTitle()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("SSU", "readingroom 안의 노트 삭제 완료");
                                db.collection("User").document(myuser.getUid()).collection("Mynotes").document(note.getNoteTitle()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                showToast("노트 삭제 완료");
                                                Intent intent = new Intent().putExtra("isSign", isSign);
                                                setResult(RESULT_OK, intent);
                                                finish();
                                            }
                                        });
                            }
                        });
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

    private void set_info()
    {
        ArrayList<TextView> idList = new ArrayList<TextView>();
        idList.add(findViewById(R.id.note_show_for_me_tag1));
        idList.add(findViewById(R.id.note_show_for_me_tag2));
        idList.add(findViewById(R.id.note_show_for_me_tag3));
        idList.add(findViewById(R.id.note_show_for_me_tag4));
        idList.add(findViewById(R.id.note_show_for_me_tag5));
        binding.noteShowForMeTitle.setText(note.getNoteTitle());
        binding.noteShowForMeUploadDate.setText(note.getUploadDate());
        binding.noteShowForMeNickname.setText(note.getUserNickname());
        binding.noteShowForMeExplainNote.setText(note.getExplainNote());
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
                Log.d("SSU", "onSuccess: 미리보기용 pdf 다운 완료");
                if (tempfile != null)
                    binding.pdfView.fromFile(tempfile)
                            .pages(0, 1, 2)
                            .enableSwipe(true) // allows to block changing pages using swipe
                            .swipeHorizontal(false)
                            .enableAnnotationRendering(true)
                            .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                            .scrollHandle(new DefaultScrollHandle(Note_show_for_me.this, true))
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

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();  //db 객체 얻기
    }
}