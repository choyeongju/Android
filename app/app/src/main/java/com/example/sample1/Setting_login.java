package com.example.noteLib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.noteLib.databinding.ActivitySettingLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Setting_login extends AppCompatActivity {

    private void showToast(String message){
        Toast toast=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ActivitySettingLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.settingEditMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Setting_login.this,Setting_primary.class));
            }
        });

        binding.settingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        binding.settingDeleteMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Setting_login.this)
                        .setMessage("정말 탈퇴하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //"예"눌렀을 때 사용자 계정 삭제하면서 로그인 화면으로 돌아가기
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("SSU", "User account deleted.");
                                                    Toast.makeText(Setting_login.this, "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Setting_login.this, Login.class));
                                                    finish();
                                                }
                                                else {
                                                    // Handle the case where user account deletion was not successful
                                                    Log.e("SSU", "User account deletion failed.", task.getException());
                                                }
                                            }
                                        });
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //"아니오" 누르면 아무것도 안하고 그냥 팝업창만 지우기
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void logout(){
        mAuth.signOut();
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Setting_login.this, Login.class));
        finish();
    }
}