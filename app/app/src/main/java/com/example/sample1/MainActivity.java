package com.example.noteLib;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.noteLib.databinding.ActivityMainBinding;
import com.example.noteLib.main.MainReadingRoomAdapter;
import com.example.noteLib.main.TableAdapter;
import com.example.noteLib.main.TableRow;
import com.example.noteLib.quiet.User;
import com.example.noteLib.readingroom.Reading_Room;
import com.example.noteLib.search.Search;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private float initTime;
    private float initY;
    public boolean isSign;
    private FirebaseFirestore db;
    private User myuser = null;
    private ActivityMainBinding binding;
    private MainReadingRoomAdapter mainReadingRoomAdapter;
    private ArrayList<TableRow> arrayList;

    public void onStart() {
        super.onStart();
        // Check if User is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            isSign = false;
            Log.d("SSU", "islogin on null: " + isSign);
            // 메인 화면 버튼 세팅(비로그인시)
            set_button(isSign);
        }
        else {
            isSign = true;
            //firestore에서 user 정보 가져오기
            getUser(currentUser);
        }
        Log.d("SSU", "Main isSign : " + isSign);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //firestore와 fireAuth 객체 가져오기
        mAuth = FirebaseAuth.getInstance();
        initializeCloudFirestore();

        String[] departmentKoArray = getResources().getStringArray(R.array.department_ko);
        TableAdapter tableAdapter = new TableAdapter(getTableRowList(departmentKoArray));
        binding.mainAllReadingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.mainAllReadingRoomRecyclerView.setAdapter(tableAdapter);

        binding.mainFavoritesRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == RESULT_OK) {
                            isSign = true;
                            myuser = (User)o.getData().getSerializableExtra("User");
                        }
                    }
                });

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
                        launcher.launch(new Intent(MainActivity.this, Reading_Room.class)   //유저 정보 가져 와야 함
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
                        launcher.launch(new Intent(MainActivity.this, Reading_Room.class)   //유저 정보 가져 와야 함
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
                launcher.launch(new Intent(MainActivity.this, Search.class)
                        .putExtra("isSign", isSign)
                        .putExtra("User", myuser));
                overridePendingTransition(0, 0);
            }
        });

        binding.goMyRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSign) {
                    startActivity(new Intent(MainActivity.this, My_room.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
                else {
                    launcher.launch(new Intent(MainActivity.this, Login.class));
                }
            }
        });

        binding.userDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSign)
                    launcher.launch(new Intent(MainActivity.this, Reading_Room.class)
                            .putExtra("isSign", isSign)
                            .putExtra("department", myuser.getDepartment())
                            .putExtra("User", myuser));
                else
                    launcher.launch(new Intent(MainActivity.this, Login.class));
            }
        });

        binding.goNewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSign)
                    launcher.launch(new Intent(MainActivity.this, New_note.class));
                else
                    launcher.launch(new Intent(MainActivity.this, Login.class));
            }
        });

        binding.goSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSign == false)
                    launcher.launch(new Intent(MainActivity.this, Login.class));
                else
                    launcher.launch(new Intent(MainActivity.this, Setting_login.class));
            }

        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(System.currentTimeMillis() - initTime > 3000)
            {
                Toast.makeText(this, "종료 할려면 한 번 더 누르세요", Toast.LENGTH_LONG).show();
                initTime = System.currentTimeMillis();
            }
            else
            {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
            //binding.mainFavoritesRecyclerView.setAdapter(mainReadingRoomAdapter);
        }
    }

    private void getUser(FirebaseUser user) {
        db.collection("User").whereEqualTo("id", user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshot) {
                        List<DocumentSnapshot> templist = queryDocumentSnapshot.getDocuments();
                        Log.d("SSU", "onSuccess: " + templist.size());
                        if (templist.size() == 1){
                            myuser = templist.get(0).toObject(User.class);
                            Log.d("SSU", myuser.getNickname());
                            set_button(isSign);
                        }
                        else
                            Toast.makeText(MainActivity.this, "데이터베이스에 아이디가 없다.", Toast.LENGTH_SHORT).show();
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
        Display display = getWindowManager().getDefaultDisplay();  // in Activity
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

}