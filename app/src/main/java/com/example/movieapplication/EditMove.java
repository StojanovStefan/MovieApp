package com.example.movieapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditMove extends AppCompatActivity {

    private EditText movieEt,descriptionEt,categoryEt,priceEt,durationEt;
    private Button btnEdit;
    private ImageButton backBtn;
    String IDMovie;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_move);

        movieEt = (EditText) findViewById(R.id.movieEt);
        descriptionEt = (EditText) findViewById(R.id.descriptionEt);
        categoryEt = (EditText) findViewById(R.id.categoryEt);
        priceEt = (EditText) findViewById(R.id.priceEt);
        durationEt = (EditText) findViewById(R.id.durationEt);

        backBtn = (ImageButton) findViewById(R.id.backBtn);

        btnEdit = (Button) findViewById(R.id.savesubmitBtn);

        IDMovie = getIntent().getExtras().getString("id");

        String title = getIntent().getExtras().getString("title");
        movieEt.setText(title);

        String description = getIntent().getExtras().getString("description");
        descriptionEt.setText(description);

        String category = getIntent().getExtras().getString("category");
        categoryEt.setText(category);

        String price = getIntent().getExtras().getString("price");
        priceEt.setText(price);

        String duration = getIntent().getExtras().getString("duration");
        durationEt.setText(duration);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Map<String, Object> map = new HashMap<>();
                map.put("title", movieEt.getText().toString());
                map.put("description", descriptionEt.getText().toString());
                map.put("category", categoryEt.getText().toString());
                map.put("price", priceEt.getText().toString());
                map.put("duration", durationEt.getText().toString());


                FirebaseDatabase.getInstance().getReference().child("Movies")
                        .child(IDMovie).updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditMove.this, "Data updated successfuly", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditMove.this,ShowMoviesAdminActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditMove.this, "Error wile updated", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditMove.this,ShowMoviesAdminActivity.class);
                startActivity(intent);
            }
        });










    }
}