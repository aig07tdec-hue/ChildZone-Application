package com.childzone.admin.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.childzone.admin.R;
import com.childzone.admin.activities.ChildDetailsActivity;
import com.childzone.admin.models.Child;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

    private List<Child> childList;
    private List<Child> childListFull;

    public ChildAdapter(List<Child> childList) {
        this.childList = new ArrayList<>(childList);
        this.childListFull = new ArrayList<>(childList);
    }

    public void updateList(List<Child> newList) {
        this.childList = new ArrayList<>(newList);
        this.childListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);

        String name = child.getChildName();
        holder.tvChildName.setText(name != null && !name.trim().isEmpty() ? name : "Unnamed Child");

        String id = child.getChildId();
        holder.tvChildId.setText("ID: #" + (id != null ? id : "N/A"));

        String age = child.getAge();
        holder.tvChildInfo.setText("Age: " + (age != null ? age : "N/A"));

        String parentName = child.getParentName();
        holder.tvParentName.setText("Parent: " + (parentName != null ? parentName : "N/A"));

        String parentEmail = child.getParentEmail();
        holder.tvParentEmail.setText(parentEmail != null ? parentEmail : "");

        holder.tvChildScore.setText(String.valueOf(child.getScore()));
        holder.tvPlayTime.setText(child.getPlayTime() + "m");
        holder.tvRewards.setText(String.valueOf(child.getRewards()));

        int progress = Math.max(0, Math.min(100, child.getProgress()));
        holder.pbChildProgress.setMax(100);
        holder.pbChildProgress.setProgress(progress);
        holder.tvProgressPercentage.setText(progress + "%");

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChildDetailsActivity.class);
            intent.putExtra("child", child);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public void filter(String text) {
        childList.clear();
        if (text == null || text.trim().isEmpty()) {
            childList.addAll(childListFull);
        } else {
            String query = text.toLowerCase().trim();
            for (Child item : childListFull) {
                String name = item.getChildName() != null ? item.getChildName().toLowerCase() : "";
                String id = item.getChildId() != null ? item.getChildId().toLowerCase() : "";
                String email = item.getParentEmail() != null ? item.getParentEmail().toLowerCase() : "";
                String parent = item.getParentName() != null ? item.getParentName().toLowerCase() : "";

                if (name.contains(query) || id.contains(query) || email.contains(query) || parent.contains(query)) {
                    childList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void showAllChildren() {
        childList.clear();
        childList.addAll(childListFull);
        notifyDataSetChanged();
    }

    public void sortByName() {
        Collections.sort(childList, (c1, c2) -> {
            String n1 = c1.getChildName() != null ? c1.getChildName() : "";
            String n2 = c2.getChildName() != null ? c2.getChildName() : "";
            return n1.compareToIgnoreCase(n2);
        });
        notifyDataSetChanged();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tvChildName, tvChildId, tvChildInfo, tvParentName, tvParentEmail;
        TextView tvChildScore, tvPlayTime, tvRewards, tvProgressPercentage;
        ProgressBar pbChildProgress;
        View btnViewDetails;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            tvChildId = itemView.findViewById(R.id.tvChildId);
            tvChildInfo = itemView.findViewById(R.id.tvChildInfo);
            tvParentName = itemView.findViewById(R.id.tvParentName);
            tvParentEmail = itemView.findViewById(R.id.tvParentEmail);
            tvChildScore = itemView.findViewById(R.id.tvChildScore);
            tvPlayTime = itemView.findViewById(R.id.tvPlayTime);
            tvRewards = itemView.findViewById(R.id.tvRewards);
            tvProgressPercentage = itemView.findViewById(R.id.tvProgressPercentage);
            pbChildProgress = itemView.findViewById(R.id.pbChildProgress);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
