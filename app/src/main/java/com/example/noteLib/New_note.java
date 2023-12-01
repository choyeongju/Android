package com.example.noteLib;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteLib.databinding.ActivityNewNoteBinding;
import com.example.noteLib.department.Choice_department;
import com.example.noteLib.quiet.Note;
import com.example.noteLib.quiet.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class New_note extends AppCompatActivity {

    private FirebaseFirestore db;
    private Note note;
    //private String noteId;
    private String newNotetitle;
    private User myuser = null;
    private String nickname;
    private String uid;
    ArrayList tagList = new ArrayList<String>();
    ArrayList mybookshelves = new ArrayList<String>();
    private String newNoteExplain;
    private boolean isprivate;
    private String bookshelf;
    private AlertDialog bookshelfdialog;
    private String department;
    private ActivityResultLauncher<Intent> launcher;
    private Uri uri;



    private void showToast(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNewNoteBinding binding = ActivityNewNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myuser = (User)getIntent().getSerializableExtra("User");
        nickname = myuser.getNickname();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        department = o.getData().getStringExtra("result");
                        binding.newNoteChoiceDepartmentButton.setText(o.getData().getStringExtra("result"));
                        showToast(department + " 선택");
                        String[] departmentArray = getResources().getStringArray(R.array.department);
                        String[] departmentKoArray = getResources().getStringArray(R.array.department_ko);
                        for(int i = 0; i <departmentArray.length; i++){
                            if (departmentKoArray[i].equals(department)){
                                department = departmentArray[i];
                                Log.d("ljs", "departmentko");
                                break;
                            }
                        }
                        Log.d("ljs", "department = " + department);
                    }
                });

        ActivityResultLauncher<Intent> findFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            // The result data contains a URI for the document or directory that
                            // the user selected.
                            uri = null;
                            if (o.getData() != null) {
                                uri = o.getData().getData();
                                Log.d("SSU", "파일 가져오기 완료 : " + uri);
                                binding.fileTag.setText(getFileNameFromUri(uri));
                                binding.fileTag.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                            Log.d("SSU", "파일 가져오기 실패");
                    }
                });
        initializeCloudFirestore();

        binding.findFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SSU", "외부저장소 읽기가능여부 : " + isExternalStorageReadable());
                Log.d("SSU", "외부저장소 쓰기가능여부 : " + isExternalStorageWritable());

                Intent openfileintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                openfileintent.addCategory(Intent.CATEGORY_OPENABLE);
                openfileintent.setType("application/pdf");


                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
                //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

                findFileLauncher.launch(openfileintent);
            }
        });

        binding.addTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTag = binding.newNoteTag.getText().toString();
                if(newTag.length() == 0) showToast("태그를 입력하세요");
                else {
                    if(tagList.size() > 4 )
                        showToast("태그는 5개까지 가능합니다");
                    else {
                        tagList.add(newTag);
                        showToast(newTag + " 태그를 추가하였습니다.");
                        String tag = tagList.toString();
                        binding.tag.setVisibility(View.VISIBLE);
                        binding.tag.setText(tag);
                    }
                }

            }
        });

        binding.isprivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    showToast("비공개를 선택하였습니다.");
                    isprivate = true;
                    department = "private";
                    binding.newNoteChoiceDepartmentButton.setVisibility(View.GONE);
                }
                else {
                    binding.newNoteChoiceDepartmentButton.setVisibility(View.VISIBLE);
                    showToast("비공개를 해제하였습니다.");
                    isprivate = false;
                }
            }
        });


        binding.newNoteChoiceDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new Intent(New_note.this, Choice_department.class)
                        .putExtra("from_new_note", 1)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

        binding.newNoteChoiceBookshelfButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mybookshelves = myuser.getMybookshelves();

                AlertDialog.Builder arrayBuilder = new AlertDialog.Builder(New_note.this);
                arrayBuilder.setTitle("책장을 선택하세요");

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        New_note.this,
                        android.R.layout.select_dialog_singlechoice);
                for(int i =0; i < mybookshelves.size(); i++) adapter.add(mybookshelves.get(i).toString());


                arrayBuilder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                arrayBuilder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                bookshelf = adapter.getItem(id).toString();

                                showToast(bookshelf + " 책장을 선택하였습니다.");
                                binding.newNoteChoiceBookshelfButton.setText(bookshelf);
                            }
                        });
                arrayBuilder.show();
            }
        }));
        binding.createNotebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                newNotetitle = binding.newNoteTitle.getText().toString();
                newNoteExplain = binding.newNoteExplain.getText().toString();
                getuseruid();

                if (newNotetitle.length() < 1){
                    showToast("제목을 입력하세요.");}

                else if (newNoteExplain.length() < 10){
                    showToast("필기에 대한 설명은 최소 10글자 이상이어야 합니다.");}
                else if (uri == null)
                    showToast("파일을 선택해 주세요.");
                else if (isprivate == false && department == null)
                    showToast("열람실을 선택해 주세요.");
                else {
                    getpdf_and_upload(uri); //note에 uri 추가
                }

            }
        });

        binding.cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                showToast("노트 생성을 취소하였습니다.");
            }
        });
    }

    private void getuseruid(){
        CollectionReference userRef = db.collection("User");
        userRef.whereEqualTo("nickname", nickname)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        User temp;
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        if (documents.size() == 0)
                            Toast.makeText(New_note.this, "로그인을 안하셨음", Toast.LENGTH_SHORT).show();
                        for (DocumentSnapshot doc: documents) {
                            uid = doc.getReference().getId();
                        }

                    }
                });
    }

    private void initializeCloudFirestore() {
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
    }

    private void uploadNote(Note note, String newNotetitle, String department, String bookshelf) { //다큐먼트 등록

        Map<String, Object> collectednote = new HashMap<>();
        collectednote.put("id", newNotetitle);
        collectednote.put("department", department);
        collectednote.put("bookshelf", bookshelf);


        db.collection("User").document(uid).collection("Mynotes").document(newNotetitle).set(collectednote);
        Log.d("SSU", "공개노트유저업뎃");
        db.collection("ReadingRoom").document(department).collection("notes")
                .document(newNotetitle).set(note);
        Log.d("SSU", "공개노트서재업뎃");
        Intent intent = new Intent().putExtra("isSign", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void uploadPrivateNote(Note note, String newNotetitle) {

        Map<String, Object> collectednote = new HashMap<>();
        collectednote.put("id", newNotetitle);
        collectednote.put("department", department);
        collectednote.put("bookshelf", bookshelf);

        db.collection("User").document(uid).collection("Mynotes").document(newNotetitle).set(collectednote);
        Log.d("ljs", "비공개노트유저업뎃");
        db.collection("ReadingRoom").document("private").collection("notes")
                .document(newNotetitle).set(note);
        Log.d("ljs", "비공개노트서재업뎃");
        Intent intent = new Intent().putExtra("isSign", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    // Checks if a volume containing external storage is available
// for read and write.
    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    private boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    private String getPath(String extension) {
        String dir = "pdf";

        String fileName = "anonymous" + "_" + System.currentTimeMillis() + "." + extension;

        return dir + "/" + fileName;
    }

    private void getpdf_and_upload(Uri uri)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pdfRef = storageRef.child(getPath("pdf"));

        UploadTask uploadTask = pdfRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("SSU", "파일 데이터 업로드 실패");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.d("SSU", "파일 데이터 업로드 성공");
                note = new Note(newNotetitle, nickname, isprivate, tagList, department, bookshelf, newNoteExplain, taskSnapshot.getMetadata().getReference().toString());
                if (isprivate == true) {
                    uploadPrivateNote(note, newNotetitle);
                    showToast(newNotetitle + " 노트를 업로드하였습니다");
                }
                else {
                        uploadNote(note, newNotetitle, department, bookshelf);
                        showToast(newNotetitle + " 노트를 업로드하였습니다");
                }
                Log.d("ljs", "note upload");

            }
        });   // Perform operations on the document using its URI.
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String bookshelf;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent().putExtra("isSign", true);
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return fileName;
    }
}