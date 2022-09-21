package com.example.movieapplication;

import static com.example.movieapplication.Constants.MAX_BYTES_PICTURE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapplication.databinding.RowMoviesAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterMovieAdmin extends RecyclerView.Adapter<AdapterMovieAdmin.HolderMoviesAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelMovie> moviesArrayList, filterList;

    private RowMoviesAdminBinding binding;

    private FilterMovieAdmin filter;

    private static final String TAG = "MOVIE_ADAPTER_TAG";

    private ProgressDialog progressDialog;

    public AdapterMovieAdmin(Context context, ArrayList<ModelMovie> moviesArrayList) {
        this.context = context;
        this.moviesArrayList = moviesArrayList;
        this.filterList = moviesArrayList;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderMoviesAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowMoviesAdminBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderMoviesAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMoviesAdmin holder, int position) {

        ModelMovie model = moviesArrayList.get(position);
        String title = model.getTitle();
        String description = model.getDescription();
        String price = model.getPrice();
        String duration = model.getDuration();
        String category = model.getCategory();
        long timestamp = model.getTimestamp();

        String formatedDate = MyApplication.formatTimestamp(timestamp);

        //set
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.priceTv.setText(price);
        holder.categoryTv.setText(category);
        holder.durationTv.setText(duration);

        //load data


        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(model,holder);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowMoviesAdminActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void moreOptionsDialog(ModelMovie model, HolderMoviesAdmin holder) {

        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder= new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){

                        }
                        else if (which==1){
                            deleteMovie(model,holder);

                        }
                    }
                }) .show();
    }

    private void deleteMovie(ModelMovie model, HolderMoviesAdmin holder) {
        String movieUrl = model.getUrl();
        String movieTitle = model.getTitle();
        String movieId = model.getId();
        Log.d(TAG,"deleteMovie: Deleting...");
        progressDialog.setMessage("Deleting " + movieTitle + "...");
        progressDialog.show();

        Log.d(TAG, "Deleting from storage..");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Movies");
        reference.child(movieId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Deleted from db too");
                        progressDialog.dismiss();
                        Toast.makeText(context, "Movie deleted successfully!", Toast.LENGTH_SHORT).show();

                    }
                }) .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: Failed to delete from db due to" + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(movieUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Deleted from Storage");
                        Log.d(TAG,"onSuccess: Now deleting info from db..");
                    }
                }) .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure: Failed to delete from Storage due to" + e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void loadPictureFromUrl(ModelMovie model, HolderMoviesAdmin holder) {

        String jpgUrl = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(jpgUrl);
        ref.getBytes(MAX_BYTES_PICTURE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: "+model.getTitle()+ " successfully got the file");

                        holder.imageView.getImageAlpha();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed getting file from url due to"+e.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return moviesArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterMovieAdmin(filterList, this);
        }
        return filter;
    }

    class HolderMoviesAdmin extends RecyclerView.ViewHolder {

        ImageView imageView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv, categoryTv, priceTv, durationTv;
        ImageButton moreBtn;

        public HolderMoviesAdmin(@NonNull View itemView) {
            super(itemView);

            imageView = binding.imageView;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            priceTv = binding.priceTv;
            durationTv=binding.durationTv;
            moreBtn = binding.moreBtn;
        }
    }
}
