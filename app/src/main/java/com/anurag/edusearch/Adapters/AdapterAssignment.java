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
import com.anurag.edusearch.Filters.FilterMaterial;
import com.anurag.edusearch.MaterialDetailsActivity;
import com.anurag.edusearch.Models.ModelAssignment;
import com.anurag.edusearch.Models.ModelMaterial;
import com.anurag.edusearch.MyApplication;
import com.anurag.edusearch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterAssignment extends RecyclerView.Adapter<AdapterAssignment.HolderAssignment> implements Filterable {

    private Context context;
    public ArrayList<ModelAssignment> assignmentList, filterList;
    private FilterAssignment filter;

    public AdapterAssignment(Context context, ArrayList<ModelAssignment> assignmentList) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.filterList = assignmentList;
    }

    @NonNull
    @Override
    public HolderAssignment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_assignment, parent, false);
        return new HolderAssignment(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderAssignment holder, int position) {

        final ModelAssignment modelAssignment = assignmentList.get(position);
        String assignmentName = modelAssignment.getAssignmentName();
        String branch = modelAssignment.getBranch();
        String semester = modelAssignment.getSemester();
        String date = modelAssignment.getTimestamp();
        String schoolId = modelAssignment.getSchoolId();
        String year = modelAssignment.getYear();
        String assignmentUrl = modelAssignment.getUrl();
        String assignmentId = modelAssignment.getAssignmentId();
        String assignedBy = modelAssignment.getAssignedBy();
        String dueDate = modelAssignment.getDueDate();
        String fullMarks = modelAssignment.getFullMarks();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(dateFormat);
        holder.dueDateTv.setText(dueDate);
        holder.assignmentName.setText(assignmentName);
        holder.assignedByTv.setText(assignedBy);

        checkSubmittedAss(schoolId, year, branch, semester, assignmentId, holder.profileIv, holder.submittedIv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, AssignmentViewActivity.class);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("assignmentUrl", assignmentUrl);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("branch", branch);
                intent.putExtra("semester", semester);
                intent.putExtra("dueDate", dueDate);
                intent.putExtra("year", year);
                intent.putExtra("fullMarks", fullMarks);
                context.startActivity(intent);

            }
        });

    }
    private void checkSubmittedAss(String schoolId, String year, String branch, String semester, String assignmentId, ImageView profileIv, ImageView submittedIv) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Schools");
        ref.child(schoolId).child("Assignment").child(year).child(branch).child(semester).child(assignmentId).child("Submission").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            profileIv.setVisibility(View.GONE);
                            submittedIv.setVisibility(View.VISIBLE);
                        }else {
                            profileIv.setVisibility(View.VISIBLE);
                            submittedIv.setVisibility(View.GONE);
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

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterAssignment(this, assignmentList);

        }
        return filter;
    }

    class HolderAssignment extends RecyclerView.ViewHolder {

        private TextView assignmentName, dueDateTv, dateTv, assignedByTv;
        private ImageView profileIv, submittedIv;

        public HolderAssignment(@NonNull View itemView) {
            super(itemView);

            assignmentName = itemView.findViewById(R.id.assignmentName);
            assignedByTv = itemView.findViewById(R.id.assignedByTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            dueDateTv = itemView.findViewById(R.id.dueDateTv);
            profileIv = itemView.findViewById(R.id.profileIv);
            submittedIv = itemView.findViewById(R.id.submittedIv);

        }
    }
}