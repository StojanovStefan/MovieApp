package com.example.movieapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapplication.databinding.ActivityDashboardUser00Binding;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardUser00Activity extends AppCompatActivity {

    private ActivityDashboardUser00Binding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUser00Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }



}
