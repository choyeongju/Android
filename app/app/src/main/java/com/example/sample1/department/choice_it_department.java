package com.example.noteLib.department;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.noteLib.R;
import com.example.noteLib.databinding.ActivityChoiceItDepartmentBinding;

public class choice_it_department extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChoiceItDepartmentBinding binding = ActivityChoiceItDepartmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.AIConvergenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("result", getResources().getString(R.string.AIConvergence)));
                finish();
            }
        });

        binding.GlobalMediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("result",getResources().getString(R.string.GlobalMedia)));
                finish();
            }
        });

        binding.MediaManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("result",getResources().getString(R.string.MediaManagement)));
                finish();
            }
        });

        binding.SoftwareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("result",getResources().getString(R.string.Software)));
                finish();
            }
        });

        binding.ElectronicITConvergenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("result", getResources().getString(R.string.ElectronicITConvergence)));
                finish();
            }
        });

        binding.ElectronicEngineeringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("result", getResources().getString(R.string.ElectronicEngineering)));
                finish();
            }
        });

        binding.ComputerScienceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("result", getResources().getString(R.string.ComputerScience)));
                finish();
            }
        });

        binding.InformationOfElectronicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra("result", getResources().getString(R.string.InformationOfElectronic)));
                finish();
            }
        });
    }
}