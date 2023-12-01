package com.example.noteLib.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.noteLib.MainActivity;
import com.example.noteLib.databinding.FragmentBookShelfBinding;
import com.example.noteLib.main.TableAdapter;
import com.example.noteLib.main.TableRow;
import com.example.noteLib.noteshow.Note_show;
import com.example.noteLib.noteshow.Note_show_for_me;
import com.example.noteLib.quiet.CollectedNote;
import com.example.noteLib.quiet.Note;
import com.example.noteLib.quiet.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BookShelfFragment extends Fragment {

    private static final String BOOKSHELFNAME = "bookshelfName";
    private static final String ITEMS = "items";
    private static final String ISSIGN = "isSign";
    private static final String MYUSER = "myuser";

    // TODO: Rename and change types of parameters
    private ActivityResultLauncher<Intent> launcher;
    private String bookshelfName;
    private ArrayList<CollectedNote> items;
    private FragmentBookShelfBinding binding;
    private ArrayList<TableRow> arrayList;
    private float initY;
    private boolean isSign;
    private User myuser;
    private FirebaseFirestore db;
    private ArrayList mybookshelves = new ArrayList<String>();
    private String chooseBookshelf;
    private String chooseCollectedNote;

    public BookShelfFragment() {
        // Required empty public constructor
    }

    public static BookShelfFragment newInstance(String bookshelfName, ArrayList<CollectedNote> items, boolean isSign, User myuser) {
        //여기 바꿔야 함
        BookShelfFragment fragment = new BookShelfFragment();
        Bundle args = new Bundle();
        args.putString(BOOKSHELFNAME, bookshelfName);
        args.putParcelableArrayList(ITEMS, (ArrayList<? extends Parcelable>) items);
        args.putBoolean(ISSIGN, isSign);
        args.putSerializable(MYUSER, myuser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            launcher = ((MainActivity)getActivity()).librarylauncher;
            bookshelfName = getArguments().getString(BOOKSHELFNAME);
            items = getArguments().getParcelableArrayList(ITEMS);
            items.removeIf(collectedNote -> (collectedNote.getId().equals("sampleid")));
            isSign = getArguments().getBoolean(ISSIGN);
            myuser = (User)getArguments().getSerializable(MYUSER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookShelfBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeCloudFirestore();

        TableAdapter tableAdapter = new TableAdapter(getTableRowList(items));
        binding.bookshelfRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.bookshelfRecyclerView.setAdapter(tableAdapter);

        binding.bookshelfRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                        CollectedNote tempNote = items.get(viewPostion * 3 + getPosition(arrayList.get(viewPostion), e.getX()));
                        db.collection("ReadingRoom").document(tempNote.getDepartment())
                                .collection("notes").whereEqualTo("noteTitle", tempNote.getId()).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Note tempNote = queryDocumentSnapshots.getDocuments().get(0).toObject(Note.class);
                                        if (tempNote == null)
                                            Log.d("SSU", "노트 가져오기 실패");
                                        if (isSign && myuser.getNickname().equals(tempNote.getUserNickname())) {  //닉네임 일치 시
                                            launcher.launch(new Intent(getContext(), Note_show_for_me.class)
                                                    .putExtra("isSign", isSign)
                                                    .putExtra("Note", tempNote)
                                                    .putExtra("User", myuser)
                                                    .putExtra("MyRoomFragment", bookshelfName));
                                        }
                                        else {
                                            launcher.launch(new Intent(getContext(), Note_show.class)
                                                    .putExtra("isSign", isSign)
                                                    .putExtra("Note", tempNote)
                                                    .putExtra("User", myuser)
                                                    .putExtra("MyRoomFragment", bookshelfName));
                                        }
                                    }
                                });
                    }
                }
                return false;
            }
            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
        binding.bookshelfMvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mybookshelves = myuser.getMybookshelves();

                AlertDialog.Builder arrayBuilder = new AlertDialog.Builder(getContext());
                arrayBuilder.setTitle("노트를 선택하세요");

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.select_dialog_singlechoice);
                for(int i =0; i < items.size(); i++) adapter.add(items.get(i).getId());
                arrayBuilder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                arrayBuilder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                chooseCollectedNote = adapter.getItem(id).toString();
                                showToast(chooseCollectedNote + " 노트를 선택하였습니다.");
                                AlertDialog.Builder arrayBuilder = new AlertDialog.Builder(getContext());
                                arrayBuilder.setTitle("책장을 선택하세요");

                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        getContext(),
                                        android.R.layout.select_dialog_singlechoice);
                                for(int i =0; i < mybookshelves.size(); i++) adapter.add(mybookshelves.get(i).toString());
                                arrayBuilder.setNegativeButton("취소",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                arrayBuilder.setAdapter(adapter,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                chooseBookshelf = adapter.getItem(id).toString();
                                                showToast(chooseBookshelf + " 책장을 선택하였습니다.");
                                                db.collection("User").document(myuser.getUid()).collection("Mynotes")
                                                        .document(chooseCollectedNote).update("bookshelf", chooseBookshelf);
                                                items.removeIf(collectedNote -> (collectedNote.getId().equals(chooseCollectedNote)));
                                                TableAdapter tableAdapter = new TableAdapter(getTableRowList(items));
                                                binding.bookshelfRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                binding.bookshelfRecyclerView.setAdapter(tableAdapter);
                                            }
                                        });
                                arrayBuilder.show();
                            }
                        });
                arrayBuilder.show();
            }
        });
    }

    private ArrayList<TableRow> getTableRowList(ArrayList<CollectedNote> items)
    {
        arrayList = new ArrayList<TableRow>();
        for (int i = 0; i < items.size(); i++) {
            if (i + 1 >= items.size())
                arrayList.add(new TableRow(items.get(i).getId(), null, null));
            else if (i + 2 >= items.size()) {
                arrayList.add(new TableRow(items.get(i).getId(), items.get(i + 1).getId(), null));
                i++;
            }
            else{
                arrayList.add(new TableRow(items.get(i).getId(), items.get(i + 1).getId(), items.get(i + 2).getId()));
                i += 2;
            }
        }
        return arrayList;
    }

    private int getPosition(TableRow tableRow, float x)
    {
        Display display = getActivity().getWindowManager().getDefaultDisplay();  // in Activity
        /* getActivity().getWindowManager().getDefaultDisplay() */ // in Fragment
        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        int width = size.x;
        Log.d("SSU", "getPosition: x = " + width + " touchx = " + x);
        if (x <= width / 3)
            return 0;
        else if (x > width / 3 && x <= width / 3 * 2)
            return 1;
        else
            return 2;
    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();  //db 객체 얻기
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}