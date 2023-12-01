package com.example.noteLib.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noteLib.MainActivity;
import com.example.noteLib.databinding.FragmentMyRoomBinding;
import com.example.noteLib.adapter.BookshelfAdapter;
import com.example.noteLib.quiet.CollectedNote;
import com.example.noteLib.quiet.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyRoomFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ISSIGN = "isSign";
    private static final String ARG_MYUSER = "myuser";
    private static final String ARG_DB = "db";
    private static final String BOOKSHELF = "bookshelf";

    // TODO: Rename and change types of parameters
    private ActivityResultLauncher<Intent> launcher;

    public boolean isSign;
    private User myuser = null;
    private String bookshelf;
    private String nickname;
    private FirebaseFirestore db;
    public FragmentMyRoomBinding binding;
    private ArrayList<CollectedNote> mynotes;
    private ArrayList<CollectedNote>[] myRoomNotes;
    protected ArrayList<String> bookshelfName;
    private BookshelfAdapter bookshelfAdapter;
    private float initY;
    private String uid;

    public MyRoomFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MyRoomFragment newInstance(boolean isSign, User myuser, @Nullable String bookshelf) {
        MyRoomFragment fragment = new MyRoomFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ISSIGN, isSign);
        args.putSerializable(ARG_MYUSER, myuser);
        if (bookshelf != null)
            args.putString(BOOKSHELF, bookshelf);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SSU", "oncreate");
        if (getArguments() != null) {
            launcher = ((MainActivity)getActivity()).librarylauncher;
            isSign = getArguments().getBoolean(ARG_ISSIGN);
            myuser = (User)getArguments().getSerializable(ARG_MYUSER);
            bookshelf = getArguments().getString(BOOKSHELF);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeCloudFirestore();
        binding.bookshelfRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        db.collection("User").whereEqualTo("nickname", myuser.getNickname())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() == 1) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                uid = doc.getId();
                                getMyNotes(doc.getId());
                            }
                        }
                        else
                            Log.d("SSU", "유저를 찾을 수 없음");
                    }
                });

        binding.addMybookshelvesBtn.setOnClickListener(new View.OnClickListener() {
            final EditText editText = new EditText(getContext());

            @Override
            public void onClick(View v) {

                AlertDialog.Builder adddialog = new AlertDialog.Builder(getContext())
                        .setTitle("추가할 책장을 입력하세요")
                        .setView(editText)
                        .setPositiveButton("생성",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String temptext = editText.getText().toString();
                                        myuser.getMybookshelves().add(temptext);
                                        Addbookshelves(temptext, uid);
                                        showToast(temptext + " 책장을 생성하였습니다.");
                                    }
                                });
                adddialog.show();
            }
        });



    }
    private void showToast(String message){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();  //db 객체 얻기
    }

    private DialogInterface.OnClickListener dialoglistener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {


        }
    };

    private void getMyNotes(String userUID) {
        db.collection("User").document(userUID).collection("Mynotes")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<CollectedNote> mynote = new ArrayList<CollectedNote>();
                        CollectedNote temp;
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        Log.d("SSU", "onSuccess: " + documents.size());
                        if (documents.size() == 0)
                            Toast.makeText(getContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        for (DocumentSnapshot doc: documents) {
                            temp = doc.toObject(CollectedNote.class);
                            mynote.add(temp);
                        }
                        mynotes = mynote;
                        ArrayList<CollectedNote>[] temp_notes =  make_mynotes_array(mynote);
                        myRoomNotes = temp_notes;
                        Log.d("SSU", "책장 갯수 : " + temp_notes.length);
                        BookshelfAdapter bookshelfAdapter_t = new BookshelfAdapter(temp_notes, bookshelfName, isSign, myuser);
                        bookshelfAdapter = bookshelfAdapter_t;
                        binding.bookshelfRecyclerview.setAdapter(bookshelfAdapter_t);
                    }
                });
    }

    private ArrayList<CollectedNote> find_bookshelfNotes(){
        for(int i = 0; i < bookshelfName.size(); i++){
            if (bookshelf.equals(bookshelfName.get(i))){
                return myRoomNotes[i];
            }
        }
        return null;
    }

    private void Addbookshelves(String bookshelfname, String uid){

        db.collection("User").document(uid).update("mybookshelves", FieldValue.arrayUnion(bookshelfname));
        Log.d("SSU", "책장추가");
        getMyNotes(uid);
    }

    private ArrayList<CollectedNote>[] make_mynotes_array(ArrayList<CollectedNote> mynote){

        int j;
        ArrayList<String> bookshelfName = myuser.getMybookshelves();

        ArrayList<CollectedNote>[] temp_notes = new ArrayList[bookshelfName.size()];
        for (int i = 0; i < bookshelfName.size(); i++){
            temp_notes[i] = new ArrayList<CollectedNote>();
        }
        for (int i = 0; i < mynote.size(); i++){
            for (j = 0; j < bookshelfName.size(); j++){
                if (mynote.get(i).getBookshelf().equals(bookshelfName.get(j))) {  //note의 책장이 bookshelfname[j]라면,
                    temp_notes[j].add(mynote.get(i));
                    break;
                }
            }
        }
        this.bookshelfName = bookshelfName;
        return temp_notes;
    }

}