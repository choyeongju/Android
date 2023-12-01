package com.example.noteLib.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.noteLib.Login;
import com.example.noteLib.MainActivity;
import com.example.noteLib.Setting_primary;
import com.example.noteLib.databinding.FragmentSettingBinding;
import com.example.noteLib.quiet.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LAUNCHER = "launcher";
    private static final String ARG_ISSIGN = "isSign";
    private static final String ARG_MYUSER = "myuser";
    private static final String ARG_DB = "db";

    // TODO: Rename and change types of parameters
    private ActivityResultLauncher<Intent> launcher;
    public boolean isSign;
    private User myuser = null;
    private FirebaseFirestore db;
    private CollectionReference userCollection;
    private FragmentSettingBinding binding;
    private String current_user_ID;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SettingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(boolean isSign, User myuser) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ISSIGN, isSign);
        args.putSerializable(ARG_MYUSER, myuser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            launcher = ((MainActivity)getActivity()).librarylauncher;
            isSign = getArguments().getBoolean(ARG_ISSIGN);
            myuser = (User)getArguments().getSerializable(ARG_MYUSER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeCloudFirestore();
        userCollection = db.collection("User");
        if (myuser != null) {
            current_user_ID = myuser.getID();
        }

        //이거 두 줄 추가 누락됐음~>~>~ 보고 넣어주세요
        binding.settingDepartment.setText(myuser.getDepartment());
        binding.settingNickname.setText(myuser.getNickname());

        binding.settingEditMyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Setting_primary.class));
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
                new AlertDialog.Builder(getActivity())
                        .setMessage("정말 탈퇴하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //"예"눌렀을 때 사용자 계정 삭제하면서 로그인 화면으로 돌아가기  (추가) firestore내의 정보도 삭제해야 한다.

                                Query query = userCollection.whereEqualTo("id",current_user_ID);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            Log.d("SSU", "documents count : " + task.getResult().size());
                                            for(QueryDocumentSnapshot document : task.getResult()){
                                                Log.d("SSU","문서 아이디 가져오기 성공");
                                                String documentID=document.getId();

                                                CollectionReference myNotesCollection =db.collection("User").document(documentID).collection("Mynotes");
                                                myNotesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            for(QueryDocumentSnapshot notreDocument : task.getResult()){
                                                                String noteDocumentID = notreDocument.getId();

                                                                myNotesCollection.document(noteDocumentID).delete();
                                                            }
                                                        }
                                                    }
                                                });

                                                db.collection("User").document(documentID)
                                                        .delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Log.d("SSU","파이어스토어 내의 정보도 삭제함");
                                                            }
                                                        });
                                            }
                                        } else{
                                            Log.d("SSU","에러발생");
                                        }
                                    }
                                });


                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("SSU", "User account deleted.");
                                                    Toast.makeText(getContext(), "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getActivity(), Login.class));
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
        Toast.makeText(getContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), Login.class));
    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();  //db 객체 얻기
    }

    private void showToast(String message){
        Toast toast=Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}