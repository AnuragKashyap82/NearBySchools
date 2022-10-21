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

import com.anurag.edusearch.FacultyDetailsActivity;
import com.anurag.edusearch.Filters.FilterCategory;
import com.anurag.edusearch.Filters.FilterMaterial;
import com.anurag.edusearch.Filters.FilterNotice;
import com.anurag.edusearch.MaterialDetailsActivity;
import com.anurag.edusearch.MaterialVievActivity;
import com.anurag.edusearch.Models.ModelCategory;
import com.anurag.edusearch.Models.ModelMaterial;
import com.anurag.edusearch.Models.ModelNotice;
import com.anurag.edusearch.NoticeViewActivity;
import com.anurag.edusearch.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterMaterial extends RecyclerView.Adapter<AdapterMaterial.HolderMaterial> implements Filterable {

    private Context context;
    public ArrayList<ModelMaterial> materialList, filterList;
    private FilterMaterial filter;

    public AdapterMaterial(Context context, ArrayList<ModelMaterial> materialList) {
        this.context = context;
        this.materialList = materialList;
        this.filterList = materialList;
    }

    @NonNull
    @Override
    public HolderMaterial onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_material, parent, false);
        return new HolderMaterial(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderMaterial holder, int position) {

        final ModelMaterial modelMaterial = materialList.get(position);
        String materialId = modelMaterial.getMaterialId();
        String branch = modelMaterial.getBranch();
        String semester = modelMaterial.getSemester();
        String date = modelMaterial.getTimestamp();
        String schoolId = modelMaterial.getSchoolId();
        String materialUrl = modelMaterial.getUrl();
        String subject = modelMaterial.getSubject();
        String topic = modelMaterial.getTopic();

        holder.subjectNameTv.setText(subject);
        holder.topicTv.setText(topic);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(dateFormat);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, MaterialDetailsActivity.class);
                intent.putExtra("materialId", materialId);
                intent.putExtra("schoolId", schoolId);
                intent.putExtra("materialUrl", materialUrl);
                intent.putExtra("branch", branch);
                intent.putExtra("semester", semester);
                intent.putExtra("topic", topic);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterMaterial(this, materialList);

        }
        return filter;
    }

    class HolderMaterial extends RecyclerView.ViewHolder {

        private TextView subjectNameTv, topicTv, dateTv;

        public HolderMaterial(@NonNull View itemView) {
            super(itemView);

            subjectNameTv = itemView.findViewById(R.id.subjectNameTv);
            topicTv = itemView.findViewById(R.id.topicTv);
            dateTv = itemView.findViewById(R.id.dateTv);

        }
    }
}