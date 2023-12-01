package com.example.noteLib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityShowUserPwBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.rxjava3.annotations.NonNull;

public class Show_user_PW extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityShowUserPwBinding binding;
    private String pw;
    private String ID_for_find_pw;


    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowUserPwBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            ID_for_find_pw = intent.getStringExtra("ID_for_find_pw");
        }


        setting_pw(ID_for_find_pw);

        binding.pwShowNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Show_user_PW.this, Login.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });
    }

    private void setting_pw(String ID_for_find_pw) {
        db.collection("User")
                .whereEqualTo("id", ID_for_find_pw)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                pw = document.getString("pw");
                            }
                            binding.userPwShow.setText(pw);
                        } else {
                            Log.d("SSU", "에러");
                        }
                    }
                });
    }
}