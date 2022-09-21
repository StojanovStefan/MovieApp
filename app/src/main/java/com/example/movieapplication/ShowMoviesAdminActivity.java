package com.example.movieapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapplication.databinding.ActivityShowMoviesAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowMoviesAdminActivity extends AppCompatActivity {

    private ActivityShowMoviesAdminBinding binding;

    private ArrayList<ModelMovie> movieArrayList;

    private AdapterMovieAdmin adapterMovieAdmin;

    private static final String TAG = "MOVIE_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowMoviesAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        loadMovieList();

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    adapterMovieAdmin.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadMovieList() {
        movieArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Movies");
        ref.orderByChild("title")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        movieArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            //get data
                            ModelMovie model = ds.getValue(ModelMovie.class);
                            movieArrayList.add(model);

                            Log.d(TAG, "onDataChange: "+model.getTitle());
                        }

                        adapterMovieAdmin = new AdapterMovieAdmin(ShowMoviesAdminActivity.this, movieArrayList);
                        binding.movieRv.setAdapter(adapterMovieAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}