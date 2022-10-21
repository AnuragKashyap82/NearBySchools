package com.anurag.edusearch.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.MaterialDetailsActivity;
import com.anurag.edusearch.Models.ModelMaterialFav;
import com.anurag.edusearch.MyApplication;
import com.anurag.edusearch.databinding.RowMaterialLaterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterMaterialFavorite extends RecyclerView.Adapter<AdapterMaterialFavorite.HolderMaterialFavorite> {

    private Context context;
    private ArrayList<ModelMaterialFav> materialFavArrayList;
    private RowMaterialLaterBinding binding;

    private static final String TAG = "FAV_BOOK_TAG";

    public AdapterMaterialFavorite(Context context, ArrayList<ModelMaterialFav> materialFavArrayList) {
        this.context = context;
        this.materialFavArrayList = materialFavArrayList;
    }

    @NonNull
    @Override
    public HolderMaterialFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowMaterialLaterBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderMaterialFavorite(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMaterialFavorite holder, int position) {

        ModelMaterialFav model = materialFavArrayList.get(position);
        String schoolId = model.getSchoolId();
        String branch = model.getBranch();
        String semester = model.getSemester();
        String materialId = model.getMaterialId();
        String topic = model.getTopic();
        String materialUrl = model.getUrl();


        loadMaterialDetails(model, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MaterialDetailsActivity.class);
                intent.putExtra("materialId", model.getMaterialId());
                intent.putExtra("semester", model.getSemester());
                intent.putExtra("branch", model.getBranch());
                intent.putExtra("materialUrl", model.getUrl());
                intent.putExtra("topic", model.getTopic());
                intent.putExtra("schoolId", model.getSchoolId());
                context.startActivity(intent);
            }
        });

        holder.removeFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove Favourite")
                        .setMessage("Are you sure want to Remove Favourite?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(FirebaseAuth.getInstance().getUid()).child("Favorites").child(materialId)
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Removed from  your favorites list....", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure( Exception e) {
                                                Toast.makeText(context, "Failed to remove from favorites due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    private void loadMaterialDetails(ModelMaterialFav model, HolderMaterialFavorite holder) {
        String schoolId = model.getSchoolId();
        String branch = model.getBranch();
        String semester = model.getSemester();
        String materialId = model.getMaterialId();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Material").child(branch).child(semester).child(materialId)

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String materialId = "" + snapshot.child("materialId").getValue();
                        String subject = "" + snapshot.child("subject").getValue();
                        String topic = "" + snapshot.child("topic").getValue();
                        String branch = "" + snapshot.child("branch").getValue();
                        String semester = "" + snapshot.child("semester").getValue();
                        String schoolId = "" + snapshot.child("schoolId").getValue();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String materialUrl = "" + snapshot.child("url").getValue();

                        model.setFavorite(true);
                        model.setSubject(subject);
                        model.setTopic(topic);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setBranch(branch);
                        model.setUrl(materialUrl);
                        model.setSchoolId(schoolId);
                        model.setSemester(semester);
                        model.setMaterialId(materialId);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(timestamp));
                        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

                        MyApplication.loadMaterialSize(""+materialUrl, holder.sizeTv);

                        holder.subjectTitleTv.setText(subject);
                        holder.topicTv.setText(topic);
                        holder.dateTv.setText(dateFormat);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return materialFavArrayList.size();
    }

    class HolderMaterialFavorite extends RecyclerView.ViewHolder{

        TextView subjectTitleTv, topicTv, sizeTv, dateTv;
        ImageButton removeFavBtn;


        public HolderMaterialFavorite(@NonNull View itemView) {
            super(itemView);

            subjectTitleTv = binding.subjectTitleTv;
            removeFavBtn = binding.removeFavBtn;
            topicTv = binding.topicTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
        }
    }

}
