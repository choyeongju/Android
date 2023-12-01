package com.example.noteLib.noteshow;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.noteLib.databinding.FragmentDownloadBinding;
import com.example.noteLib.quiet.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class DownloadFragment extends Fragment {
    private ProgressBar spinner;
    private Note note;
    FragmentDownloadBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentDownloadBinding binding = FragmentDownloadBinding.inflate(inflater, container, false);
        if (getArguments() != null)
            note = (Note)getArguments().getSerializable("note");

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl(note.getNoteUri()); // from gs://~~~

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        File destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File tempfile = null;
        try {
            tempfile = File.createTempFile("pdf", ".pdf", destinationPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gsReference.getFile(tempfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                showToast("다운로드 완료");
                Log.d("SSU", "download Success = " + gsReference.getName());
                Bundle bundle = new Bundle();
                bundle.putBoolean("bundleKey", true);
                getParentFragmentManager().setFragmentResult("requestKey", bundle);
                removeFragment(DownloadFragment.this);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("다운로드 실패");
                Log.d("SSU", "download fail..");
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {

            }
        });
    }

    private void removeFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
            final FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.remove(fragment);
            mFragmentTransaction.commit();
            fragment.onDestroy();
            fragment.onDetach();
            fragment = null;
        }
    }

    private void showToast(String message){
        Toast toast= Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT);
        toast.show();
    }

}
