package com.childzone.admin.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.childzone.admin.databinding.ActivityReportDetailsBinding;
import com.childzone.admin.models.Report;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportDetailsActivity extends AppCompatActivity {

    private ActivityReportDetailsBinding binding;
    private FirebaseFirestore db;
    private String reportId;
    private Report currentReport;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        reportId = getIntent().getStringExtra("report_id");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupStatusSpinner();
        loadReportDetails();

        binding.btnUpdateReport.setOnClickListener(v -> updateReport());
    }

    private void setupStatusSpinner() {
        String[] statuses = {"Pending", "In Progress", "Resolved"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        binding.spinnerStatus.setAdapter(adapter);
    }

    private void loadReportDetails() {
        if (reportId == null) {
            finish();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("reports").document(reportId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);
                    currentReport = documentSnapshot.toObject(Report.class);
                    if (currentReport != null) {
                        displayData();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void displayData() {
        binding.tvDetailCategory.setText(currentReport.getCategory());
        binding.tvDetailSubject.setText(currentReport.getSubject());
        binding.tvDetailUser.setText(currentReport.getUserName() + " (" + currentReport.getUserType() + ")");
        binding.tvDetailChildId.setText("Child ID: " + (currentReport.getChildId() != null ? currentReport.getChildId() : "-"));
        binding.tvDetailDescription.setText(currentReport.getDescription());
        
        if (currentReport.getCreatedAt() != null) {
            binding.tvDetailDate.setText("Submitted: " + dateFormat.format(currentReport.getCreatedAt().toDate()));
        }

        if (currentReport.getResolvedAt() != null) {
            binding.tvResolvedDate.setVisibility(View.VISIBLE);
            binding.tvResolvedDate.setText("Resolved: " + dateFormat.format(currentReport.getResolvedAt().toDate()));
        }

        binding.spinnerStatus.setText(currentReport.getStatus(), false);
        if (currentReport.getAdminReply() != null) {
            binding.etAdminReply.setText(currentReport.getAdminReply());
        }
    }

    private void updateReport() {
        String newStatus = binding.spinnerStatus.getText().toString();
        String reply = binding.etAdminReply.getText().toString().trim();

        if (reply.isEmpty()) {
            Toast.makeText(this, "Please enter a reply", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("adminReply", reply);

        if ("Resolved".equals(newStatus)) {
            updates.put("resolvedAt", FieldValue.serverTimestamp());
        } else {
            updates.put("resolvedAt", null);
        }

        db.collection("reports").document(reportId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Report updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
