package com.example.noteLib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityShowPwHintBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Show_PW_hint extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityShowPwHintBinding binding;
    private String PWhint_answer;
    private String ID_for_find_pw;

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowPwHintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //비밀번호 힌트 텍스트뷰에 띄움
        Intent intent = getIntent();
        if (intent != null) {
            ID_for_find_pw = intent.getStringExtra("ID_for_find_pw");
        }
        setting_pw_hint(getIntent().getStringExtra("ID_for_find_pw"));

        binding.pwHintNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PWhint_answer = binding.pwHintAnswerInputbox.getText().toString();
                if (PWhint_answer.length() == 0) {
                    Log.d("SSU", "정답 입력바람");
                    showToast("정답을 입력해주세요.");
                } else {
                    checkAnswerforPWhint(PWhint_answer,ID_for_find_pw);
                }
            }
        });
    }

    private void checkAnswerforPWhint(String PWhint_answer, String ID_for_find_pw) {

        db.collection("User")
                .whereEqualTo("id", ID_for_find_pw)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String PWhint_field=document.getString("pw_hint_input");
                            if (!Objects.equals(PWhint_answer, PWhint_field)) {
                                Log.d("SSU","틀린 정답임");
                              showToast("틀린 정답입니다. 정답을 다시 입력해주세요.");
                            } else {
                                Log.d("SSU","정답임");
                                startActivity(new Intent(Show_PW_hint.this, Show_user_PW.class)
                                        .putExtra("ID_for_find_pw", ID_for_find_pw)
                                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
                            }
                        }
                    } else {
                        Log.d("SSU", "실패");
                        showToast("오류가 발생하였습니다");
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
                            String PWhint = document.getString("pw_hint");
                            //ID_for_find_pw에 해당하는 문서의 pw_hint필드의 값을 가져옴

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





