package com.example.noteLib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityFindIdBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Find_ID extends AppCompatActivity {

    private ActivityFindIdBinding binding;
    private String Nickname_for_find_id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void showToast(String message){
        Toast toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.findIdNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nickname_for_find_id=binding.findNicknameInputbox.getText().toString();
                if(Nickname_for_find_id.length()==0){
                    Log.d("SSU","닉네임 입력바람");
                    showToast("닉네임을 입력해주세요.");
                }
                else{
                    checkNICKNAMEforFindPW(Nickname_for_find_id);
                }
            }
        });
    }

    private void checkNICKNAMEforFindPW(String Nickname_for_find_id){
        db.collection("User")
                .whereEqualTo("nickname",Nickname_for_find_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Log.d("SSU", "존재하지 않는 닉네임");
                                showToast("존재하지 않는 닉네임입니다.");
                            } else {
                                Log.d("SSU", "존재하는 닉네임");
                                showToast("존재하는 닉네임입니다.");
                                startActivity(new Intent(Find_ID.this, Show_user_ID.class)
                                        .putExtra("Nickname_for_find_id",Nickname_for_find_id)
                                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                            }
                        } else {
                            Log.d("SSU", "실패");
                            showToast("오류가 발생하였습니다");
                        }
                    }
                });
    }
}