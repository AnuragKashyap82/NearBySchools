package com.anurag.edusearch.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.Filters.FilterImage;
import com.anurag.edusearch.ImageLikesDetailsActivity;
import com.anurag.edusearch.ImageViewActivity;
import com.anurag.edusearch.Models.ModeImage;
import com.anurag.edusearch.MyApplication;
import com.anurag.edusearch.R;
import com.anurag.edusearch.databinding.DialogCommentAddBinding;
import com.anurag.edusearch.ImageCommentDetailsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.anurag.edusearch.Constants.MAX_BYTES_PDF;

public class AdapterImage extends RecyclerView.Adapter<AdapterImage.HolderImage> implements Filterable {

    private Context context;
    public ArrayList<ModeImage> imagesList, filterList;
    private FilterImage filter;
    private String comment = "";
    boolean isLiked = false;


    public AdapterImage(Context context, ArrayList<ModeImage> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
        this.filterList = imagesList;
    }

    @NonNull
    @Override
    public HolderImage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_image, parent, false);
        return new HolderImage(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderImage holder, int position) {

        final ModeImage modelImage = imagesList.get(position);
        String galleryId = modelImage.getGalleryId();
        String image = modelImage.getImage();
        String imageId = modelImage.getImageId();
        String schoolId = modelImage.getSchoolId();

        countComments(schoolId, galleryId, imageId, holder);
        countDownloads(schoolId, galleryId, imageId, holder);
        countLikes(schoolId, galleryId, imageId, holder);
//        MyApplication.checkIsLiked(schoolId, imageId, galleryId, holder, isLiked);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Likes").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isLiked = snapshot.exists();
                        if (isLiked) {
                            holder.likeBtn.setImageResource(R.drawable.ic_like_primary);
                        } else {
                            holder.likeBtn.setImageResource(R.drawable.ic_like_outline);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_gallery_primary).into(holder.imageIv);
        } catch (Exception e) {
            holder.imageIv.setImageResource(R.drawable.ic_gallery_primary);
        }

        holder.imageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("galleryId", galleryId);
                intent.putExtra("imageId", imageId);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("image", image);
                context.startActivity(intent);
            }
        });
        holder.noComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageCommentDetailsActivity.class);
                intent.putExtra("galleryId", galleryId);
                intent.putExtra("imageId", imageId);
                intent.putExtra("schoolId", schoolId);
                context.startActivity(intent);
            }
        });
        holder.noLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageLikesDetailsActivity.class);
                intent.putExtra("galleryId", galleryId);
                intent.putExtra("imageId", imageId);
                intent.putExtra("schoolId", schoolId);
                context.startActivity(intent);
            }
        });
        FirebaseRemoteConfig lFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings lConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        lFirebaseRemoteConfig.setConfigSettingsAsync(lConfigSettings);
        lFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                boolean isLikeImageEnabled = lFirebaseRemoteConfig.getBoolean("isLikeImageEnabled");
                if (isLikeImageEnabled) {
                    holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isLiked) {
                                MyApplication.removeLikedImage(context, "" + schoolId, "" + galleryId, "" + imageId);
                            } else {
                                MyApplication.likeImage(context, "" + schoolId, "" + galleryId, "" + imageId);
                            }
                        }
                    });
                } else {
                    holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "This Features is Temporarily disabled...!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                boolean isDownloadImageEnabled = mFirebaseRemoteConfig.getBoolean("isDownloadImageEnabled");
                if (isDownloadImageEnabled) {
                    holder.downloadImageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Download")
                                    .setMessage("Do you want to Download Image: " + imageId + " ?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            MyApplication.downloadImage(context, "" + imageId, "" + schoolId, "" +image , "" + galleryId);
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
                } else {
                    holder.downloadImageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "This Features is Temporarily disabled...!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogCommentAddBinding commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(context));

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);
                builder.setView(commentAddBinding.getRoot());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        comment = commentAddBinding.commentEt.getText().toString().trim();
                        if (TextUtils.isEmpty(comment)) {
                            Toast.makeText(context, "Enter your comment....", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();
                            MyApplication.addComment(context, comment, imageId, galleryId, schoolId);
                        }
                    }
                });
            }
        });
    }

    public void countComments(String schoolId, String galleryId, String imageId, HolderImage holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long numberOfComments = snapshot.getChildrenCount();
                        float numberComments = numberOfComments / 1;
                        holder.noComment.setText(String.format("%.0f", numberComments));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void countDownloads(String schoolId, String galleryId, String imageId, HolderImage holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();
                        holder.noDownload.setText(downloadsCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void countLikes(String schoolId, String galleryId, String imageId, HolderImage holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Likes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long numberOfLikes = snapshot.getChildrenCount();
                        float numberLikes = numberOfLikes / 1;

                        holder.noLike.setText(String.format("%.0f", numberLikes));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterImage(this, filterList);

        }
        return filter;
    }

    public class HolderImage extends RecyclerView.ViewHolder {

        private ImageView imageIv;
        public ImageButton likeBtn;
        private ImageButton downloadImageBtn;
        private ImageButton commentBtn;
        private TextView noLike, noComment, noDownload;


        public HolderImage(@NonNull View itemView) {
            super(itemView);

            imageIv = itemView.findViewById(R.id.imageIv);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            noLike = itemView.findViewById(R.id.noLike);
            downloadImageBtn = itemView.findViewById(R.id.downloadImageBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            noComment = itemView.findViewById(R.id.noComment);
            noDownload = itemView.findViewById(R.id.noDownload);
        }
    }
}
