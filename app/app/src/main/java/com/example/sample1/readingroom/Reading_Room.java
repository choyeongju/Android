package com.example.noteLib.readingroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteLib.My_room;
import com.example.noteLib.R;
import com.example.noteLib.databinding.ActivityReadingRoomBinding;
import com.example.noteLib.noteshow.Note_show;
import com.example.noteLib.noteshow.Note_show_for_me;
import com.example.noteLib.quiet.Note;
import com.example.noteLib.quiet.User;
import com.example.noteLib.search.RecycleNoteAdapter;
import com.example.noteLib.search.Search;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Reading_Room extends AppCompatActivity {

    public static ArrayList<Note> noteList = new ArrayList<Note>();
    //private NoteAdapter noteAdapter;

    private RecycleNoteAdapter recycleNoteAdapter;
    private ActivityReadingRoomBinding binding;
    private boolean isSign;
    private String department;
    private String department_ko;
    private FirebaseFirestore db;
    private float initY;
    private User myuser = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityReadingRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeCloudFirestore();

        isSign = getIntent().getBooleanExtra("isSign", false);
        department_ko = getIntent().getStringExtra("department");
        myuser = (User)getIntent().getSerializableExtra("User");
        Log.d("SSU", "Reading_Room isSign : " + isSign);

        String[] departmentArray = getResources().getStringArray(R.array.department);
        String[] departmentKoArray = getResources().getStringArray(R.array.department_ko);
        int i;
        for (i = 0; i < departmentKoArray.length; i++)
        {
            if (departmentKoArray[i].equals(department_ko)){
                department = departmentArray[i];
                break;
            }
        }
        if (i == departmentKoArray.length)
            department = department_ko;
        binding.readingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(Reading_Room.this));
        recycleNoteAdapter = new RecycleNoteAdapter(noteList);
        binding.readingRoomName.setText(department_ko);

        print_readingRoom_notes();
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == RESULT_OK)
                            isSign = true;
                    }
                });

        binding.searchBtn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() != 0) {
                    doMySearch(s, department);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        binding.readingRoomRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    initY = e.getRawY();
                }
                else if (e.getAction() == MotionEvent.ACTION_UP) //화면에 손을 땟을 때
                {
                    float diff = initY - e.getRawY();
                    if (diff <= 5 && diff >= -5) {
                        View selectView = rv.findChildViewUnder(e.getX(), e.getY());
                        int viewPostion = rv.getChildLayoutPosition(selectView);
                        Log.d("SSU", "userNickName = " + myuser.getNickname() + ", NoteUserNickname = " + noteList.get(viewPostion).getUserNickname());
                        if (isSign && myuser.getNickname().equals(noteList.get(viewPostion).getUserNickname())) {  //닉네임 일치 시
                            launcher.launch(new Intent(Reading_Room.this, Note_show_for_me.class)
                                    .putExtra("isSign", isSign)
                                    .putExtra("Note", noteList.get(viewPostion))
                                    .putExtra("User", myuser));
                        }
                        else {
                            launcher.launch(new Intent(Reading_Room.this, Note_show.class)
                                    .putExtra("isSign", isSign)
                                    .putExtra("Note", noteList.get(viewPostion))
                                    .putExtra("User", myuser));
                        }
                    }
                }
                return false;
            }
            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }

    private void doMySearch(String query, String department)
    {
        Toast.makeText(this, query + " Searching...", Toast.LENGTH_SHORT).show();
        noteList.clear();
        db.collection("ReadingRoom").document(department).collection("notes")
                .where(Filter.or(
                        Filter.and(Filter.equalTo("isPrivate", false), Filter.equalTo("noteTitle", query)),
                        Filter.and(Filter.equalTo("isPrivate", false), Filter.equalTo("userNickname", query))
                ))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Note temp;
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        Log.d("SSU", "onSuccess: " + documents.size());
                        if (documents.size() == 0)
                            Toast.makeText(Reading_Room.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        for (DocumentSnapshot doc: documents) {
                            temp = doc.toObject(Note.class);
                            noteList.add(temp);
                            Log.d("SSU", temp.getNoteTitle());
                        }
                        binding.readingRoomRecyclerView.setAdapter(recycleNoteAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SSU", "error on collection group query: " + e.getMessage());
                    }
                });
    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();  //db 객체 얻기
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent().putExtra("isSign", isSign);
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    private void print_readingRoom_notes(){
        noteList.clear();
        db.collection("ReadingRoom").document(department).collection("notes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Note temp;
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        Log.d("SSU", "onSuccess: " + documents.size());
                        if (documents.size() == 0)
                            Toast.makeText(Reading_Room.this, "열람실에 노트가 없습니다.", Toast.LENGTH_SHORT).show();
                        for (DocumentSnapshot doc: documents) {
                            temp = doc.toObject(Note.class);
                            noteList.add(temp);
                            Log.d("SSU", temp.getNoteTitle());
                        }
                        binding.readingRoomRecyclerView.setAdapter(recycleNoteAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SSU", "error on collection group query: " + e.getMessage());
                    }
                });
    }
}