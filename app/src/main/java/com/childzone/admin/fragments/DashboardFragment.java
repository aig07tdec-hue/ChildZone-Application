package com.childzone.admin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.childzone.admin.databinding.FragmentDashboardBinding;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseFirestore db;
    private ListenerRegistration snapshotListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Listen for data changes
        listenForData();
    }

    private void listenForData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        snapshotListener = db.collection("children").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!isAdded()) return;

                if (error != null) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (value != null) {
                    int totalChildren = value.size();
                    long totalScore = 0;
                    double sumProgress = 0;
                    long totalPlayTime = 0;
                    long totalRewards = 0;

                    for (QueryDocumentSnapshot doc : value) {
                        Long score = doc.getLong("score");
                        if (score != null) totalScore += score;

                        Number progress = (Number) doc.get("progress");
                        if (progress != null) sumProgress += progress.doubleValue();

                        Long playTime = doc.getLong("playTime");
                        if (playTime != null) totalPlayTime += playTime;

                        Long rewards = doc.getLong("rewards");
                        if (rewards != null) totalRewards += rewards;
                    }

                    double avgProgress = totalChildren > 0 ? sumProgress / totalChildren : 0;
                    int progressPercent = (int) Math.round(avgProgress);

                    // Update UI
                    binding.tvChildrenCount.setText(String.valueOf(totalChildren));
                    binding.tvTotalScore.setText(String.valueOf(totalScore));
                    binding.tvAvgProgress.setText(String.format(Locale.getDefault(), "%d%%", progressPercent));
                    binding.tvTotalPlaytime.setText(String.format(Locale.getDefault(), "%dh", totalPlayTime));
                    binding.tvTotalRewards.setText(String.valueOf(totalRewards));

                    binding.pbWeeklyEngagement.setMax(100);
                    binding.pbWeeklyEngagement.setProgress(Math.max(0, Math.min(100, progressPercent)));

                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (snapshotListener != null) {
            snapshotListener.remove();
        }
        binding = null;
    }
}
