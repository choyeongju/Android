package com.example.noteLib.department;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.noteLib.databinding.ActivityChoiceDepartmentBinding;

public class Choice_department extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChoiceDepartmentBinding binding = ActivityChoiceDepartmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goChoiceItDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Choice_department.this, choice_it_department.class)
                        .putExtra("from_new_note", 1)
                        .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT));
            }
        });

        binding.goChoiceManageDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.goChoiceEconomyDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.goChoiceEngineeringDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.goChoiceLawDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.goChoiceSocialDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.goChoiceLiberalDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.goChoiceNaturalDepartmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}