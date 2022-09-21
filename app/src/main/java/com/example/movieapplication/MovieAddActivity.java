package com.example.movieapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapplication.databinding.ActivityMovieAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class MovieAddActivity extends AppCompatActivity {

    public ActivityMovieAddBinding binding;

    public FirebaseAuth firebaseAuth;

    private Uri jpgUri = null;

    static private final int PICTURE_PICK_CODE = 1000;

    private ProgressDialog progressDialog;


    private static final String TAG = "ADD_MOVIE_TAG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPictureIntent();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
                finish();
                startActivity(new Intent(MovieAddActivity.this, DashboardAdminActivity.class));;
            }
        });

    }

    private void addPictureIntent() {
        Log.d(TAG, "addPictureIntent: starting picture add intent");

        Intent intent = new Intent();
        intent.setType("application/jpg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"),PICTURE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_PICK_CODE) {
                Log.d(TAG, "onActivityResult: Picture Picked");

                jpgUri = data.getData();
                Log.d(TAG, "onActivityResult: URI: "+jpgUri);
            }
        }
        else {
            Log.d(TAG, "onActivityResult: cancelled picking picture");
            Toast.makeText(this, "cancelled picking picture", Toast.LENGTH_SHORT).show();
        }
    }

    private String title = "", description = "", category = "", price = "", duration = "";

    private void validateData() {

        Log.d(TAG, "validateData: validating data...");

        title = binding.movieEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        category = binding.categoryEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        duration = binding.durationEt.getText().toString().trim();


        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter title...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter description...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Enter price...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(duration)) {
            Toast.makeText(this, "Enter duration...", Toast.LENGTH_SHORT).show();
        }
        else if (jpgUri==null) {
            Toast.makeText(this, "Pick picture...", Toast.LENGTH_SHORT).show();
        }
        else {
            uploadMovieToStorage();
        }
    }

    private void uploadMovieToStorage() {
        Log.d(TAG, "uploadMovieToStorage: uploading to storage");

        progressDialog.setMessage("Uploading Movie...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        String filePathAndName = "Movies/" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(jpgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Movie uploaded to storage...");
                        Log.d(TAG, "onSuccess: getting movie url...");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedMovieUrl = ""+uriTask.getResult();

                        uploadMovieInfoToDb(uploadedMovieUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Movie upload failed due to "+e.getMessage());
                        Toast.makeText(MovieAddActivity.this, "Movie upload failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadMovieInfoToDb(String uploadedMovieUrl, long timestamp) {
        Log.d(TAG, "uploadMovieToStorage: uploading to firebase db...");
        progressDialog.setMessage("Uploading movie info...");

        String uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+ uid);
        hashMap.put("id", ""+timestamp);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("category", ""+category);
        hashMap.put("price", ""+price);
        hashMap.put("duration", ""+duration);
        hashMap.put("url", ""+uploadedMovieUrl);
        hashMap.put("timestamp", timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Movies");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Successfully uploaded...");
                        Toast.makeText(MovieAddActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db due to"+e.getMessage());
                        Toast.makeText(MovieAddActivity.this, "Failed to upload to db due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}