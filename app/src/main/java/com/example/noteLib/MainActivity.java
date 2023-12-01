package com.example.noteLib;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.noteLib.databinding.ActivityMainBinding;
import com.example.noteLib.quiet.User;
import com.example.noteLib.fragment.LibaryFragment;
import com.example.noteLib.fragment.MyRoomFragment;
import com.example.noteLib.fragment.SettingFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private float initTime;

    private boolean isSign;
    private FirebaseFirestore db;
    private User myuser = null;
    private ActivityMainBinding binding;
    public ActivityResultLauncher<Intent> librarylauncher;
    private String bookshelf;



    public void onStart() {
        super.onStart();
        // Check if User is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            isSign = false;
            Log.d("SSU", "islogin on null: " + isSign);
            transferTo(LibaryFragment.newInstance(isSign, myuser)); //myuser null
            // 메인 화면 버튼 세팅(비로그인시)
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

        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }

        librarylauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK && o.getData().getStringExtra("result") != null) { // 즐겨찾기 학과 선택
                            String choiceDepartment = o.getData().getStringExtra("result");
                            Log.d("SSU", "onActivityResult: " + o.getData().getStringExtra("result"));
                            ArrayList<String> tempfavorites = myuser.getFavorites();
                            tempfavorites.add(choiceDepartment);
                            myuser.setFavorites(tempfavorites);
                            db.collection("User").document(myuser.getUid()).update("favorites", FieldValue.arrayUnion(choiceDepartment));
                        }
                        else if (o.getResultCode() == RESULT_OK && o.getData().getStringExtra("MyRoomFragment") != null){
                            binding.bottomNavigation.setSelectedItemId(R.id.page_2);
                            Log.d("SSU", "onActivityResult: goMyroom");
                            bookshelf = o.getData().getStringExtra("MyRoomFragment");
                            transferTo(MyRoomFragment.newInstance(isSign, myuser, bookshelf));
                        }
                        else {
                            binding.bottomNavigation.setSelectedItemId(R.id.page_1);
                            isSign = o.getData().getBooleanExtra("isSign", false);
                            Log.d("SSU", "onActivityResult: isSign : " + isSign);
                            myuser = (User)o.getData().getSerializableExtra("User");
                        }
                    }
                });


        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.page_1) {
                    if (isSign)
                        transferTo(LibaryFragment.newInstance(isSign, myuser));
                    return true;
                }

                if (itemId == R.id.page_2) {
                    if (isSign)
                        transferTo(MyRoomFragment.newInstance(isSign, myuser, null));
                    else
                        librarylauncher.launch(new Intent(MainActivity.this, Login.class));
                    return true;
                }
                if (itemId == R.id.page_3) {
                    if (isSign)
                        librarylauncher.launch(new Intent(MainActivity.this, New_note.class)
                                .putExtra("User", myuser));
                    else
                        librarylauncher.launch(new Intent(MainActivity.this, Login.class));
                    return true;
                }

                if (itemId == R.id.page_4) {
                    if (isSign)
                        transferTo(SettingFragment.newInstance(isSign, myuser));
                    else
                        librarylauncher.launch(new Intent(MainActivity.this, Login.class));
                    return true;
                }
                return false;
            }
        });

        binding.bottomNavigation.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

            }
        });

    }

    private void getUser(FirebaseUser user) {
        db.collection("User").whereEqualTo("id", user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshot) {
                        User user;
                        List<DocumentSnapshot> templist = queryDocumentSnapshot.getDocuments();
                        Log.d("SSU", "onSuccess: " + templist.size());
                        if (templist.size() == 1){
                            user = templist.get(0).toObject(User.class);
                            myuser = user;
                            Log.d("SSU", user.getNickname());
                            if (bookshelf == null)
                                transferTo(LibaryFragment.newInstance(isSign, user));
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

    private void transferTo(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void showToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}