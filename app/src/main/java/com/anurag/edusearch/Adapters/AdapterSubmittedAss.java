package com.anurag.edusearch.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.anurag.edusearch.AssignmentViewActivity;
import com.anurag.edusearch.Filters.FilterAssignment;
import com.anurag.edusearch.Models.ModelAssignment;
import com.anurag.edusearch.Models.ModelSubmittedAss;
import com.anurag.edusearch.R;
import com.anurag.edusearch.SubmittedAssViewActivity;
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

public class AdapterSubmittedAss extends RecyclerView.Adapter<AdapterSubmittedAss.HolderSubmittedAss> {

    private Context context;
    public ArrayList<ModelSubmittedAss> assignmentList, filterList;
    private FilterAssignment filter;

    public AdapterSubmittedAss(Context context, ArrayList<ModelSubmittedAss> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.filterList = assignmentList;
    }

    @NonNull
    @Override
    public HolderSubmittedAss onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_submitted_ass, parent, false);
        return new HolderSubmittedAss(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderSubmittedAss holder, int position) {

        final ModelSubmittedAss modelAssignment = assignmentList.get(position);
        String branch = modelAssignment.getBranch();
        String semester = modelAssignment.getSemester();
        String date = modelAssignment.getTimestamp();
        String schoolId = modelAssignment.getSchoolId();
        String year = modelAssignment.getYear();
        String submittedAssUrl = modelAssignment.getUrl();
        String assignmentId = modelAssignment.getAssignmentId();
        String dueDate = modelAssignment.getDueDate();
        String uid = modelAssignment.getUid();
        String fullMarks = modelAssignment.getFullMarks();
        String marksObtained = modelAssignment.getMarksObtained();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        loadStudentsDetails(modelAssignment, holder);

        holder.dateTv.setText(dateFormat);
        holder.obtainedMarksTv.setText(marksObtained);
        holder.fullMarksTv.setText(fullMarks);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, SubmittedAssViewActivity.class);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("submittedAssUrl", submittedAssUrl);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("branch", branch);
                intent.putExtra("semester", semester);
                intent.putExtra("dueDate", dueDate);
                intent.putExtra("year", year);
                intent.putExtra("uid", uid);
                context.startActivity(intent);

            }
        });

    }

    private void loadStudentsDetails(ModelSubmittedAss modelAssignment, HolderSubmittedAss holder) {
        String uid = modelAssignment.getUid();

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

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }


    class HolderSubmittedAss extends RecyclerView.ViewHolder {

        private TextView nameTv, dateTv, obtainedMarksTv, fullMarksTv;
        private ImageView profileTv;

        public HolderSubmittedAss(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            profileTv = itemView.findViewById(R.id.profileTv);
            obtainedMarksTv = itemView.findViewById(R.id.obtainedMarksTv);
            fullMarksTv = itemView.findViewById(R.id.fullMarksTv);

        }
    }
}