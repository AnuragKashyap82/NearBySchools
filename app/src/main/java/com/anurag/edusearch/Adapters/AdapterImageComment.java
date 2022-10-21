package com.anurag.edusearch.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.Models.ModelComment;
import com.anurag.edusearch.Models.ModelImageComment;
import com.anurag.edusearch.R;
import com.anurag.edusearch.databinding.RowCommentBinding;
import com.anurag.edusearch.databinding.RowImageCommentDetailsBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterImageComment extends RecyclerView.Adapter<AdapterImageComment.HolderComment> {

    private Context context;
    private ArrayList<ModelImageComment> commentArrayList;
    private FirebaseAuth firebaseAuth;
    private RowImageCommentDetailsBinding binding;

    public AdapterImageComment(Context context, ArrayList<ModelImageComment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowImageCommentDetailsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {
        ModelImageComment modelComment = commentArrayList.get(position);
        String commentId = modelComment.getCommentId();
        String comment = modelComment.getComment();
        String uid = modelComment.getUid();
        String timestamp = modelComment.getTimestamp();
        String galleryId = modelComment.getGalleryId();
        String imageId = modelComment.getImageId();
        String schoolId = modelComment.getSchoolId();

        loadUserDetails(modelComment, holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(dateFormat);
        holder.commentTv.setText(comment);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() != null){
//                    deleteComment(modelComment, holder);
                    Toast.makeText(context, "Comments on This Image....!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void loadUserDetails(ModelImageComment modelComment, HolderComment holder) {
        String uid = modelComment.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        holder.nameTv.setText(name);
                        try {
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(holder.profileTv);
                        }
                        catch (Exception e){
                            holder.profileTv.setImageResource(R.drawable.ic_person_gray);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    private void deleteComment(ModelComment modelComment, HolderComment holder) {
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Delete Comment")
//                .setMessage("Are you sure want to delete this comment")
//                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
//                        ref.child(modelComment.getBookId())
//                                .child("Comments")
//                                .child(modelComment.getCommentId())
//                                .removeValue()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "Deleted....", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(context, "Failed to delete due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                })
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .show();
//    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder{

        ShapeableImageView profileTv;
        TextView nameTv, dateTv, commentTv;

        public HolderComment(@NonNull View itemView) {
            super(itemView);

            profileTv = binding.profileTv;
            nameTv = binding.nameTv;
            dateTv = binding.dateTv;
            commentTv = binding.commentTv;
        }
    }

}
