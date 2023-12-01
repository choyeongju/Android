package com.example.noteLib;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.noteLib.databinding.ActivityShowPwHintBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Show_PW_hint extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityShowPwHintBinding binding;
    private String PWhint;

    private Boolean pw_answer_is_correct = false;

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowPwHintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        if (intent != null) {
            String ID_for_find_pw = intent.getStringExtra("ID_for_find_pw");
        }
        setting_pw_hint(getIntent().getStringExtra("ID_for_find_pw")); //비밀번호 힌트 텍스트뷰에 띄움
        //아이디 잘 받아왔는지 걍 확인용(이거 주석해제하고 실행해보면 잘 받아온거 확인가능함)
        // binding.pwHintQuestionShow.setText(getIntent().getStringExtra("ID_for_find_pw"));


        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                    }
                });

        binding.pwHintNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //여기다가 비밀번호 힌트의 정답이 맞는지 확인하는 코드 넣으면 됨
//
//                if(pw_answer_is_correct) {
//                    launcher.launch(new Intent(Show_PW_hint.this, Show_user_PW.class));
//                }
//                else {
//                    Log.d("SSU", "비밀번호 힌트의 정답이 틀렸습니다.");
//                    showToast("비밀번호 힌트의 정답이 틀렸습니다.");
//                }
            }
        });
    }

    //비밀먼호 힌트 세팅하는 함수
    private void setting_pw_hint(String ID_for_find_pw) {
        db.collection("User")
                .whereEqualTo("id", ID_for_find_pw)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String PWhint = document.getString("pw_hint"); //ID_for_find_pw에 해당하는 문서의 pw_hint필드의 값을 가져옴
                            if (PWhint != null) {
                                Log.d("SSU", "비밀번호 힌트 찾았음");
                                binding.pwHintQuestionShow.setText(PWhint);
                            } else {
                                Log.d("SSU", "비밀번호 힌트 찾을 수 없음");
                                showToast("비밀번호 힌트를 찾을 수 없습니다.");
                            }
                        }
                    } else {
                        Log.e("SSU", "Error : ", task.getException());
                        showToast("에러 발생");
                    }
                });
    }

}



