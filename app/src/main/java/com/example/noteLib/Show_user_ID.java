package com.example.noteLib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityShowUserIdBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.rxjava3.annotations.NonNull;

public class Show_user_ID extends AppCompatActivity {

    private ActivityShowUserIdBinding binding;
    private String Nickname_for_find_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityShowUserIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
           Nickname_for_find_id = intent.getStringExtra("Nickname_for_find_id");
        }


        setting_id(Nickname_for_find_id);

        binding.idShowNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Show_user_ID.this, Login.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });
    }

    private void setting_id(String Nickname_for_find_id){
        db.collection("User")
                .whereEqualTo("nickname", Nickname_for_find_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                id = document.getString("id");
                            }
                            binding.userIdShow.setText(id);
                        } else {
                            Log.d("SSU", "에러");
                        }
                    }
                });
    }
}