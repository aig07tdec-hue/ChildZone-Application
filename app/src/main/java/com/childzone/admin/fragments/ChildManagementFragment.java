package com.childzone.admin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.childzone.admin.adapters.ChildAdapter;
import com.childzone.admin.databinding.FragmentChildManagementBinding;
import com.childzone.admin.models.Child;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildManagementFragment extends Fragment {

    private static final String TAG = "ChildManagementFragment";
    private FragmentChildManagementBinding binding;
    private ChildAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference childrenRef;
    private ListenerRegistration snapshotListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChildManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        childrenRef = db.collection("children");

        setupRecyclerView();
        setupSearchView();
        setupFilterButton();
        listenForChildUpdates();
    }

    private void setupFilterButton() {
        binding.btnFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenu().add("Show All Children");
            popup.getMenu().add("Sort by Name (A-Z)");

            popup.setOnMenuItemClickListener(item -> {
                CharSequence title = item.getTitle();
                if (title != null && "Show All Children".equals(title.toString())) {
                    binding.searchView.setQuery("", false);
                    binding.searchView.clearFocus();
                    adapter.showAllChildren();
                    Toast.makeText(requireContext(), "Showing all children", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (title != null && "Sort by Name (A-Z)".equals(title.toString())) {
                    adapter.sortByName();
                    Toast.makeText(requireContext(), "Sorted by name", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void setupRecyclerView() {
        adapter = new ChildAdapter(new ArrayList<>());
        binding.rvChildren.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChildren.setAdapter(adapter);
    }

    private void listenForChildUpdates() {
        binding.progressBar.setVisibility(View.VISIBLE);
        snapshotListener = childrenRef.addSnapshotListener((value, error) -> {
            if (!isAdded()) return;

            binding.progressBar.setVisibility(View.GONE);

            if (error != null) {
                Log.e(TAG, "Error fetching children from Firestore", error);
                Toast.makeText(requireContext(), "Unable to load children: " + error.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            if (value != null) {
                if (value.isEmpty()) {
                    seedDefaultChildIfEmpty();
                } else {
                    List<Child> updatedList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        try {
                            Child child = Child.fromDocument(doc);
                            if (child != null) {
                                updatedList.add(child);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing child document: " + doc.getId(), e);
                        }
                    }
                    Log.d(TAG, "Fetched " + updatedList.size() + " children from Firestore");
                    adapter.updateList(updatedList);
                    binding.tvEmptyState.setVisibility(updatedList.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    private void seedDefaultChildIfEmpty() {
        Log.d(TAG, "Children collection is empty, seeding default child CZ10001...");
        String defaultId = "CZ10001";
        Map<String, Object> childData = new HashMap<>();
        childData.put("childId", defaultId);
        childData.put("childName", "Vedant");
        childData.put("age", "10");
        childData.put("parentName", "Parent Admin");
        childData.put("parentEmail", "vca12tdec@gmail.com");
        childData.put("parentMobile", "9876543210");
        childData.put("score", 150);
        childData.put("progress", 75);
        childData.put("playTime", 45);
        childData.put("rewards", 12);
        childData.put("eyeComfortEnabled", false);
        childData.put("createdAt", Timestamp.now());

        childrenRef.document(defaultId).set(childData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Successfully seeded default child CZ10001"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to seed default child", e));
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
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
