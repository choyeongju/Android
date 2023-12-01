package com.example.noteLib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityShowUserPwBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class Show_user_PW extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityShowUserPwBinding binding;
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityShowUserPwBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                    }
                });


        binding.pwShowNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(Show_user_PW.this, Login.class));
            }
        });
    }

//    private void showPW(String ID_for_find_pw) {
//        db.collection("User")
//                .whereEqualTo("id", ID_for_find_pw)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (DocumentSnapshot document : task.getResult()) {
//                             pw = document.getString("PW");
//                            if (pw != null) {
//                                Log.d("Show_user_PW", "Password for ID " + ID_for_find_pw + ": " + pw);
//                                binding.userPwShow.setText(pw);
//                            } else {
//                                Log.d("Show_user_PW", "Password not found for ID " + ID_for_find_pw);
//                            }
//                        }
//                    } else {
//                        Log.e("Show_user_PW", "Error : ", task.getException());
//                    }
//                });
//    }
}