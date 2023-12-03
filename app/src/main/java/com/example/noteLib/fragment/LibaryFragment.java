package com.example.noteLib.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.noteLib.Login;
import com.example.noteLib.MainActivity;
import com.example.noteLib.R;
import com.example.noteLib.databinding.FragmentLibaryBinding;
import com.example.noteLib.department.Choice_department;
import com.example.noteLib.adapter.MainReadingRoomAdapter;
import com.example.noteLib.main.TableAdapter;
import com.example.noteLib.main.TableRow;
import com.example.noteLib.quiet.User;
import com.example.noteLib.readingroom.Reading_Room;
import com.example.noteLib.search.Search;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LibaryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ISSIGN = "isSign";
    private static final String ARG_MYUSER = "myuser";
    private static final String ARG_DB = "db";

    // TODO: Rename and change types of parameters
    private ActivityResultLauncher<Intent> launcher;
    public boolean isSign;
    private User myuser = null;
    private FirebaseFirestore db;
    private FragmentLibaryBinding binding;
    private MainReadingRoomAdapter mainReadingRoomAdapter;
    private ArrayList<TableRow> arrayList;
    private float initY;

    public LibaryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LibaryFragment newInstance(boolean isSign, User myuser) {
        LibaryFragment fragment = new LibaryFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ISSIGN, isSign);
        args.putSerializable(ARG_MYUSER, myuser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            launcher = ((MainActivity)getActivity()).librarylauncher;
            isSign = getArguments().getBoolean(ARG_ISSIGN);
            myuser = (User)getArguments().getSerializable(ARG_MYUSER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLibaryBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeCloudFirestore();

        String[] departmentKoArray = getResources().getStringArray(R.array.department_ko);
        TableAdapter tableAdapter = new TableAdapter(getTableRowList(departmentKoArray));

        binding.mainAllReadingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.mainAllReadingRoomRecyclerView.setAdapter(tableAdapter);
        binding.mainFavoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        set_button(isSign);
        binding.mainFavoritesRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                        launcher.launch(new Intent(getContext(), Reading_Room.class)   //유저 정보 가져 와야 함
                                .putExtra("isSign", isSign)
                                .putExtra("department", myuser.getFavorites().get(viewPostion))
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
        binding.mainAllReadingRoomRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
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
                        launcher.launch(new Intent(getContext(), Reading_Room.class)   //유저 정보 가져 와야 함
                                .putExtra("isSign", isSign)
                                .putExtra("department", departmentKoArray[viewPostion * 3 + getPosition(arrayList.get(viewPostion), e.getX())])
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

        binding.mainSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(new Intent(getContext(), Search.class)
                        .putExtra("isSign", isSign)
                        .putExtra("User", myuser));
                getActivity().overridePendingTransition(0, 0);
            }
        });

        binding.addFavoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSign) {
                    launcher.launch(new Intent(getContext(), Choice_department.class)
                            .putExtra("from_LibraryFragment", 1)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                }
                else
                    launcher.launch(new Intent(getContext(), Login.class));
            }
        });

        binding.userDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSign)
                    launcher.launch(new Intent(getContext(), Reading_Room.class)
                            .putExtra("isSign", isSign)
                            .putExtra("department", myuser.getDepartment())
                            .putExtra("User", myuser));
                else
                    launcher.launch(new Intent(getContext(), Login.class));
            }
        });
    }

    private void set_button(boolean isSign) {
        Button userDepartmentButton = binding.userDepartmentButton;
        View addFavoritesBtn = binding.addFavoritesBtn;
        if (isSign) {
            Log.d("SSU", "set_button: " + myuser.getDepartment());
            userDepartmentButton.setText(myuser.getDepartment());
            userDepartmentButton.setClickable(true);
            addFavoritesBtn.setClickable(true);
            mainReadingRoomAdapter = new MainReadingRoomAdapter(myuser.getFavorites());
            binding.mainFavoritesRecyclerView.setAdapter(mainReadingRoomAdapter);
        }
        else
        {
            userDepartmentButton.setText("필기도서관");
            addFavoritesBtn.setClickable(false);
        }
    }

    private ArrayList<TableRow> getTableRowList(String[] koArray)
    {
        arrayList = new ArrayList<TableRow>();
        for (int i = 0; i < koArray.length; i++) {
            if (i + 1 >= koArray.length)
                arrayList.add(new TableRow(koArray[i], null, null));
            else if (i + 2 >= koArray.length) {
                arrayList.add(new TableRow(koArray[i], koArray[i + 1], null));
                i++;
            }
            else{
                arrayList.add(new TableRow(koArray[i], koArray[i + 1], koArray[i + 2]));
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
}