package com.childzone.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.childzone.admin.R;
import com.childzone.admin.activities.ReportDetailsActivity;
import com.childzone.admin.adapters.AdminReportsAdapter;
import com.childzone.admin.databinding.FragmentReportsBinding;
import com.childzone.admin.models.Report;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private FirebaseFirestore db;
    private AdminReportsAdapter adapter;
    private List<Report> allReports = new ArrayList<>();
    private List<Report> filteredReports = new ArrayList<>();
    private ListenerRegistration reportsListener;
    private String currentStatusFilter = "All";
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        setupRecyclerView();
        setupSearchAndFilter();
        listenForReports();
    }

    private void setupRecyclerView() {
        adapter = new AdminReportsAdapter(filteredReports, report -> {
            Intent intent = new Intent(requireContext(), ReportDetailsActivity.class);
            intent.putExtra("report_id", report.getReportId());
            startActivity(intent);
        });
        binding.rvReports.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReports.setAdapter(adapter);
    }

    private void setupSearchAndFilter() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query.toLowerCase().trim();
                applyFilters();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText.toLowerCase().trim();
                applyFilters();
                return false;
            }
        });

        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            
            int id = checkedIds.get(0);
            if (id == R.id.chipAll) currentStatusFilter = "All";
            else if (id == R.id.chipPending) currentStatusFilter = "Pending";
            else if (id == R.id.chipInProgress) currentStatusFilter = "In Progress";
            else if (id == R.id.chipResolved) currentStatusFilter = "Resolved";
            
            applyFilters();
        });
    }

    private void listenForReports() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        reportsListener = db.collection("reports")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (!isAdded()) return;
                    
                    binding.progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        allReports.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Report report = doc.toObject(Report.class);
                            if (report.getReportId() == null) report.setReportId(doc.getId());
                            allReports.add(report);
                        }
                        applyFilters();
                    }
                });
    }

    private void applyFilters() {
        filteredReports.clear();
        for (Report report : allReports) {
            boolean matchesStatus = currentStatusFilter.equals("All") || 
                                   (report.getStatus() != null && report.getStatus().equals(currentStatusFilter));
            
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    (report.getSubject() != null && report.getSubject().toLowerCase().contains(currentSearchQuery)) ||
                    (report.getCategory() != null && report.getCategory().toLowerCase().contains(currentSearchQuery)) ||
                    (report.getUserName() != null && report.getUserName().toLowerCase().contains(currentSearchQuery)) ||
                    (report.getChildId() != null && report.getChildId().toLowerCase().contains(currentSearchQuery)) ||
                    (report.getDescription() != null && report.getDescription().toLowerCase().contains(currentSearchQuery));

            if (matchesStatus && matchesSearch) {
                filteredReports.add(report);
            }
        }
        adapter.notifyDataSetChanged();
        binding.tvEmptyState.setVisibility(filteredReports.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reportsListener != null) {
            reportsListener.remove();
        }
        binding = null;
    }
}
