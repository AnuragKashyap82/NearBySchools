package com.anurag.edusearch;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.edusearch.Adapters.AdapterImage;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import androidx.annotation.NonNull;

import static com.anurag.edusearch.Constants.MAX_BYTES_PDF;

public class MyApplication extends Application {

    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void loadMaterialSize(String materialUrl, TextView sizeTv) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(materialUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();

                        double kb = bytes / 1024;
                        double mb = kb / 1024;

                        if (mb >= 1) {
                            sizeTv.setText(String.format("%.2f", mb) + " MB");
                        } else if (kb >= 1) {
                            sizeTv.setText(String.format("%.2f", kb) + " KB");
                        } else {
                            sizeTv.setText(String.format("%.2f", bytes) + " bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //    public static void checkIsLiked(String schoolId, String imageId, String galleryId, boolean isLiked, AdapterImage.HolderImage holder) {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
//        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Likes").child(firebaseAuth.getUid())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        isLiked = snapshot.exists();
//                        if (isLiked) {
//                            holder.likeBtn.setImageResource(R.drawable.ic_like_primary);
//                        } else {
//                            holder.likeBtn.setImageResource(R.drawable.ic_like_outline);
//                        }
////                        if (snapshot.exists()){
////                           boolean isLiked = true;
////                            holder.likeBtn.setImageResource(R.drawable.ic_like_primary);
////                        }else {
////                           boolean isLiked = false;
////                            holder.likeBtn.setImageResource(R.drawable.ic_like_outline);
////                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
    public static void removeLikedImage(Context context, String schoolId, String galleryId, String imageId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Likes").child(firebaseAuth.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Removed from Liked images....!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context, "Failed to remove from favorites due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void likeImage(Context context, String schoolId, String galleryId, String imageId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imageId", "" + imageId);
        hashMap.put("galleryId", "" + galleryId);
        hashMap.put("uid", "" + firebaseAuth.getUid());
        hashMap.put("schoolId", "" + schoolId);
        hashMap.put("timestamp", "" + timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Likes").child(firebaseAuth.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Added to Liked list....", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add to favorites due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void downloadImage(Context context, String imageId, String schoolId, String image, String galleryId) {

        String nameWithExtension = imageId + ".png";

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Downloading" + nameWithExtension + "....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        saveDownloadedBook(context, progressDialog, bytes, nameWithExtension, imageId, schoolId, galleryId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to download due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static void saveDownloadedBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String imageId, String schoolId, String galleryId) {
        try {
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdir();

            String filePath = downloadsFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            incrementBookDownloadCount(galleryId, imageId, schoolId);

        } catch (Exception e) {
            Toast.makeText(context, "Failed saving to download folder due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private static void incrementBookDownloadCount(String galleryId, String imageId, String schoolId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String downloadsCount = "" + snapshot.child("downloadsCount").getValue();

                        if (downloadsCount.equals("") || downloadsCount.equals("null")) {
                            downloadsCount = "0";
                        }

                        long newDownloadsCount = Long.parseLong(downloadsCount) + 1;

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("downloadsCount", newDownloadsCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Schools");
                        reference.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void addComment(Context context, String comment, String imageId, String galleryId, String schoolId) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String timestamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", "" + comment);
        hashMap.put("commentId", "" + firebaseAuth.getUid());
        hashMap.put("imageId", "" + imageId);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("galleryId", "" + galleryId);
        hashMap.put("schoolId", "" + schoolId);
        hashMap.put("uid", "" + firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Gallery").child(galleryId).child("Images").child(imageId).child("Comments").child(firebaseAuth.getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Comment Added....", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, "Failed to add comment due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
