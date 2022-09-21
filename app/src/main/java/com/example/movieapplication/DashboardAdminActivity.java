package com.example.movieapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapplication.databinding.ActivityDashboardAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardAdminActivity extends AppCompatActivity {

    private ActivityDashboardAdminBinding binding;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();

            }
        });
        binding.addMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, MovieAddActivity.class));
            }
        });

        binding.showMovieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, ShowMoviesAdminActivity.class));
            }
        });



    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        else {
            String email = firebaseUser.getEmail();
            binding.SubTitleTv.setText(email);
        }

    }
}