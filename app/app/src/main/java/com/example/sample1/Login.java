package com.example.noteLib;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.noteLib.databinding.ActivityLoginBinding;
import com.example.noteLib.quiet.Note;
import com.example.noteLib.quiet.User;
import com.example.noteLib.search.Search;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String ID;
    private String PW;
    private FirebaseFirestore db;
    private User myuser;
    private void showToast(String message){
        Toast toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == RESULT_OK)
                        {
                            myuser = (User)o.getData().getSerializableExtra("User");
                            Intent intent = new Intent().putExtra("isSign", true).putExtra("User", myuser);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ID = binding.IDBtn.getText().toString();
                PW = binding.PWBtn.getText().toString();
                Log.d("SSU", "ID : " + ID);
                Log.d("SSU", "PW : " + PW);

                if(ID.length()==0&&PW.length()==0){
                    Log.d("SSU", "아이디와 비밀번호를 입력하세요");
                    showToast("아이디와 비밀번호를 입력하세요");
                }
                else if (ID.length() == 0) {
                    Log.d("SSU", "아이디를 입력하세요");
                    showToast("아이디를 입력하세요");
                }
                else if (PW.length() == 0) {
                    Log.d("SSU", "비밀번호를 입력하세요");
                    showToast("비밀번호를 입력하세요");
                }
                else {
                    Log.d("SSU", "PW : " + PW);
                    signIn(ID, PW);
                }
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(Login.this, Join_the_membership.class));
            }
        });

        binding.findPWBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(Login.this, Find_PW.class));
            }
        });

//        //추후 구현예정
//        binding.findIDBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                launcher.launch(new Intent(Login.this, Find_ID.class));
//            }
//        });
    }

    private void signIn(String email, String password) {
        Log.d("SSU", "signIn: ");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Log.d("SSU", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the User.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Log.d("SSU", "signInWithEmail:failure");
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            initializeCloudFirestore();
            Log.d("SSU", "updateUI: " + user.getEmail());
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
                            }
                            else
                                Toast.makeText(Login.this, "데이터베이스에 아이디가 없다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("SSU", "error on collection group query: " + e.getMessage());
                        }
                    });
            Intent intent = new Intent().putExtra("isSign", true).putExtra("User", myuser);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent().putExtra("isSign", false);
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return true;
    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();  //db 객체 얻기
    }
}