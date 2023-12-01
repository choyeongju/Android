package com.example.noteLib;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityJoinTheMembershipBinding;
import com.example.noteLib.department.Choice_department;
import com.example.noteLib.quiet.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Join_the_membership extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityJoinTheMembershipBinding binding;
    private String ID;
    private boolean checkID = false;
    private boolean checkNickname = false;

    private String PW;
    private String PWCheck;

    private String join_department;
    private User user;

    AlertDialog PWhintlist;
    AlertDialog Collegelist;

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    DialogInterface.OnClickListener dialogLister = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("SSU", "onclick");
            if (dialog == PWhintlist) {
                String[] datas = getResources().getStringArray(R.array.PW_hint);
                binding.PWHintBtn.setText(datas[which]);
                showToast(datas[which] + "을 선택하셨습니다.");
            }
            if(dialog == Collegelist){
                String[] datas = getResources().getStringArray(R.array.choice_college);
                binding.jointhemembershipChoiceCollegeButton.setText(datas[which]);
                showToast(datas[which] + "를 선택하셨습니다.");
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinTheMembershipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.PWHintBtn.setOnClickListener(View -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("비밀번호 힌트 질문");
            builder.setSingleChoiceItems(R.array.PW_hint, 0, dialogLister);
            builder.setPositiveButton("확인", null);
            PWhintlist = builder.create();
            PWhintlist.show();
        });

        binding.jointhemembershipChoiceCollegeButton.setOnClickListener(View->{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("학교를 선택하세요");
            builder.setSingleChoiceItems(R.array.choice_college,0,dialogLister);
            builder.setPositiveButton("확인",null);
            Collegelist=builder.create();
            Collegelist.show();
        });

        binding.IDCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = binding.IDinputbox.getText().toString();
                if (ID.length() == 0) {
                    Log.d("SSU", "ID를 입력해주세요.");
                    showToast("ID를 입력해주세요.");
                } else {
                    checkIDAvailable(ID);
                }
            }
        });

        binding.NicknameCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Nickname = binding.Nickinputbox.getText().toString();
                if (Nickname.length() == 0) {
                    Log.d("SSU", "닉네임을 입력해주세요.");
                    showToast("닉네임을 입력해주세요.");
                } else {
                    checkNicknameAvailable(Nickname);
                }
            }
        });

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        join_department = o.getData().getStringExtra("result");
                        binding.jointhemembershipChoiceDepartmentButton.setText(o.getData().getStringExtra("result"));
                        showToast(join_department + " 선택");

                    }
                });

        binding.jointhemembershipChoiceDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(Join_the_membership.this, Choice_department.class)
                        .putExtra("from_new_note", 1)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

        binding.completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PW = binding.PWinputbox.getText().toString();
                PWCheck = binding.PWcheckinputbox.getText().toString();
                Log.d("SSU", "ID : " + ID);
                if (checkID == false) {
                    Log.d("SSU", "아이디 중복확인 해주세요.");
                    showToast("아이디 중복확인 해주세요.");
                } else if (checkNickname == false) {
                    Log.d("SSU", "닉네임 중복확인 해주세요.");
                    showToast("닉네임 중복확인 해주세요.");
                } else if (PW.length() < 6) {
                    Log.d("SSU", "비밀번호는 최소 6글자 이상이여야 합니다.");
                    showToast("비밀번호는 최소 6글자 이상이여야 합니다.");
                } else if (PWCheck.length() < 6 || PWCheck.equals(PW) == false) {
                    Log.d("SSU", "비밀번호가 일치하지 않습니다.");
                    showToast("비밀번호가 일치하지 않습니다.");
                } else if (join_department == null) {
                    Log.d("SSU", "학과를 선택해주세요.");
                    showToast("학과를 선택해주세요.");
                } else if (binding.PWHintInputbox.getText().toString().length() == 0) {
                    Log.d("SSU", "비밀번호 힌트를 입력해주세요.");
                    showToast("비밀번호 힌트를 입력해주세요.");
                } else {
                    ArrayList<String> temp = new ArrayList<>();
                    signUp(ID, PW);
                    temp.add(join_department);

                    ArrayList<String> mybookshelves = new ArrayList<>();
                    mybookshelves.add("내 필기");
                    user = new User(ID, PW, binding.PWHintBtn.getText().toString(), binding.PWHintInputbox.getText().toString(),
                            join_department, binding.Nickinputbox.getText().toString(), temp, mybookshelves, null);
                    addData(user);
                }
            }
        });
    }

    private void addData(User user) {
        db.collection("User")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SSU", "DocumentSnapshot added with ID: " + documentReference.getId());
                        showToast("회원가입 성공");
                        db.collection("User").document(documentReference.getId()).update("uid", documentReference.getId());
                        user.setUid(documentReference.getId());
                        makeBookshelf(documentReference.getId());
                    }
                });
    }

    private void makeBookshelf(String id) {
        Map<String, Object> samplenote = new HashMap<>();
        samplenote.put("department", "ComputerScience");
        samplenote.put("id", "sampleid");
        samplenote.put("bookshelf", "내 필기");
        db.collection("User").document(id)
                .collection("Mynotes").document("samplenote").set(samplenote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("SSU", "onSuccess: Mynotes 생성 완료");
                    }
                });
    }

    private void checkNicknameAvailable(final String enteredNickname) {
        db.collection("User")
                .whereEqualTo("nickname", enteredNickname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                Log.d("SSU", "이미 사용 중인 닉네임입니다.");
                                showToast("이미 사용 중인 닉네임입니다.");
                                checkNickname = false;
                            } else {
                                Log.d("SSU", "사용 가능한 닉네임입니다.");
                                showToast("사용 가능한 닉네임입니다.");
                                checkNickname = true;
                            }
                        } else {
                            Log.d("SSU", "실패했습니다", task.getException());
                            checkNickname = false;
                        }
                    }
                });
    }

    private void checkIDAvailable(final String enteredID) {
        db.collection("User")
                .whereEqualTo("id", enteredID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                Log.d("SSU", "이미 사용 중인 ID입니다.");
                                showToast("이미 사용 중인 ID입니다.");
                                checkID = false;
                            } else {
                                Log.d("SSU", "사용 가능한 ID입니다.");
                                showToast("사용 가능한 ID입니다.");
                                checkID = true;
                            }
                        } else {
                            Log.d("SSU", "실패했습니다", task.getException());
                            checkID = false;
                        }
                    }
                });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SSU", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.d("SSU", "createUserWithEmail:failure");
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent().putExtra("isSign", true).putExtra("User", user);
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

}