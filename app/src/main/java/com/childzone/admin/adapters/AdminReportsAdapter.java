package com.childzone.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.childzone.admin.R;
import com.childzone.admin.models.Report;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminReportsAdapter extends RecyclerView.Adapter<AdminReportsAdapter.ViewHolder> {

    private List<Report> reportList;
    private final OnItemClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(Report report);
    }

    public AdminReportsAdapter(List<Report> reportList, OnItemClickListener listener) {
        this.reportList = reportList;
        this.listener = listener;
    }

    public void updateList(List<Report> newList) {
        this.reportList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.tvSubject.setText(report.getSubject());
        holder.tvCategory.setText(report.getCategory());
        holder.tvUserName.setText(report.getUserName());
        holder.tvUserType.setText("User Type: " + report.getUserType());
        holder.tvStatus.setText(report.getStatus());
        
        // Status Color Coding (Professional B&W style or subtle accents)
        if ("Pending".equals(report.getStatus())) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray_light));
        } else if ("Resolved".equals(report.getStatus())) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        }

        if (report.getCreatedAt() != null) {
            holder.tvDate.setText(dateFormat.format(report.getCreatedAt().toDate()));
        } else {
            holder.tvDate.setText("-");
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(report));
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvCategory, tvUserType, tvUserName, tvStatus, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvUserType = itemView.findViewById(R.id.tvUserType);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
