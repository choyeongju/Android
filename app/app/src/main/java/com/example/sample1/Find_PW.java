package com.example.noteLib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityFindPwBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Find_PW extends AppCompatActivity {

    private ActivityFindPwBinding binding;
    private String ID_for_find_pw;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void showToast(String message){
        Toast toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFindPwBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.findPwNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID_for_find_pw=binding.findIdInputbox.getText().toString();
                if(ID_for_find_pw.length()==0){
                    Log.d("SSU","ID 입력바람");
                    showToast("ID를 입력해주세요.");
                }
                else{
                    checkIDforFindPW(ID_for_find_pw);
                }
            }
        });
    }

    private boolean checkIDforFindPW(String ID_for_find_pw){
        boolean is_Avaliable = false;
        //아이디가 db내에 존재하는지 확인
        db.collection("User")
                .whereEqualTo("id",ID_for_find_pw)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Log.d("SSU", "존재하지 않는 아이디");
                                showToast("존재하지 않는 ID입니다.");
                            } else {
                                Log.d("SSU", "존재하는 아이디");
                                showToast("존재하는 아이디입니다.");
                                startActivity(new Intent(Find_PW.this, Show_PW_hint.class)
                                        .putExtra("ID_for_find_pw", ID_for_find_pw));
                                //ID_for_find_pw를 Show_PW_hint로 넘겨줌. 이걸 통해서 db에서 PW_hint를 가져올 것임
                            }
                        } else {
                            Log.d("SSU", "실패");
                            showToast("오류가 발생하였습니다");
                        }
                    }
                });
        return true;
    }
}