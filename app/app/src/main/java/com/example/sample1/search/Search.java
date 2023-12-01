package com.example.noteLib.search;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.noteLib.databinding.ActivitySearchBinding;
import com.example.noteLib.noteshow.Note_show;
import com.example.noteLib.noteshow.Note_show_for_me;
import com.example.noteLib.quiet.Note;
import com.example.noteLib.quiet.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    public static ArrayList<Note> noteList = new ArrayList<Note>();
    private User myuser = null;
    private ActivitySearchBinding binding;
    private RecycleNoteAdapter recycleNoteAdapter;
    private boolean isSign;
    private FirebaseFirestore db;
    private float initY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        isSign = intent.getBooleanExtra("isSign", false);
        myuser = (User)intent.getSerializableExtra("User");
        Log.d("SSU", "Search isSign : " + isSign);

        initializeCloudFirestore();
        //addDataFromCustomObject();
        binding.noteRecyclerView.setLayoutManager(new LinearLayoutManager(Search.this));
        recycleNoteAdapter = new RecycleNoteAdapter(noteList);

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
                    doMySearch(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        binding.noteRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                        Log.d("SSU", "viewposition : " + viewPostion);
                        if (isSign && myuser.getNickname().equals(noteList.get(viewPostion).getUserNickname()))  //닉네임 일치 시
                            launcher.launch(new Intent(Search.this, Note_show_for_me.class)
                                    .putExtra("isSign", isSign)
                                    .putExtra("Note", noteList.get(viewPostion))
                                    .putExtra("User", myuser));
                        else
                            launcher.launch(new Intent(Search.this, Note_show.class)
                                    .putExtra("isSign", isSign)
                                    .putExtra("Note", noteList.get(viewPostion))
                                    .putExtra("User", myuser));
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

    private void doMySearch(String query)
    {
        Toast.makeText(this, query + " Searching...", Toast.LENGTH_SHORT).show();
        noteList.clear();
        db.collectionGroup("notes").where(Filter.or(
                Filter.and(Filter.equalTo("isPrivate", false), Filter.equalTo("noteTitle", query)),
                Filter.and(Filter.equalTo("isPrivate", false), Filter.equalTo("userNickname", query))
                )).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Note temp;
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        Log.d("SSU", "onSuccess: " + documents.size());
                        if (documents.size() == 0)
                            Toast.makeText(Search.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        for (DocumentSnapshot doc: documents) {
                            temp = doc.toObject(Note.class);
                            noteList.add(temp);
                            Log.d("SSU", temp.getNoteTitle());
                        }
                        binding.noteRecyclerView.setAdapter(recycleNoteAdapter);
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

    private void addDataFromCustomObject() {
        Note note1 = new Note("Algebra Basics", "user123",false , null, "Quiet Room", "Shelf A", "Introduction to algebraic concepts.", null);
        Note note2 = new Note("Newton's Laws", "user456", false, null, "Study Hall", "Shelf B", "Overview of Newton's laws of motion.", null);
        Note note3 = new Note("Java Programming", "user789", true, null, "Computer Lab", "Shelf C", "Introduction to Java programming language.", null);
        Note note4 = new Note("World War II", "user101", false, null, "History Room", "Shelf D", "Detailed study of World War II events.", null);
        Note note5 = new Note("Chemical Reactions", "user202", false, null, "Chemistry Lab", "Shelf E", "Exploration of various chemical reactions.", null);
        Note note6 = new Note("Shakespearean Sonnets", "user303", false, null, "Literature Room", "Shelf F", "Analysis of Shakespeare's sonnets.", null);
        Note note7 = new Note("Supply and Demand", "user404", false, null, "Economics Lab", "Shelf G", "Basic principles of supply and demand.", null);
        Note note8 = new Note("Cell Biology", "user505", false, null, "Biology Lab", "Shelf H", "Overview of cell biology concepts.",null);
        Note note9 = new Note("Behavioral Psychology", "user606", false, null, "Psychology Room", "Shelf I", "Study of behavioral psychology theories.",null);
        Note note10 = new Note("Renaissance Art", "user707", false, null, "Art Studio", "Shelf J", "Exploration of Renaissance art movements.",null);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note1").set(note1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("SSU", "DocumentSnapshot added with ID: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("SSU", "Error adding document", e);
            }
        });
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note2").set(note2);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note3").set(note3);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note4").set(note4);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note5").set(note5);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note6").set(note6);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note7").set(note7);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note8").set(note8);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note9").set(note9);
        db.collection("ReadingRoom").document("ComputerScience").collection("notes").document("note10").set(note10);
    }
}