package com.childzone.admin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.childzone.admin.adapters.GameAdapter;
import com.childzone.admin.databinding.FragmentGameManagementBinding;
import com.childzone.admin.models.Game;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManagementFragment extends Fragment implements GameAdapter.OnGameToggleListener {

    private static final String TAG = "GameManagementFragment";
    private FragmentGameManagementBinding binding;
    private GameAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference gameConfigRef;
    private ListenerRegistration snapshotListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGameManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        gameConfigRef = db.collection("gameConfig");

        setupRecyclerView();
        seedInitialGamesIfNeeded();
        listenToGameConfigs();
    }

    private void setupRecyclerView() {
        adapter = new GameAdapter(new ArrayList<>(), this);
        binding.rvGames.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGames.setAdapter(adapter);
    }

    private void seedInitialGamesIfNeeded() {
        // Predefined 5 actual implemented games
        List<Game> defaultGames = new ArrayList<>();
        defaultGames.add(new Game("math_quiz", "Math Quiz", "Educational", "Test your math skills.", "Easy", true));
        defaultGames.add(new Game("word_builder", "Word Builder", "Educational", "Arrange letters to form words.", "Medium", true));
        defaultGames.add(new Game("memory_match", "Memory Match", "Memory", "Match pairs of cards.", "Medium", true));
        defaultGames.add(new Game("alphabet_learning", "Alphabet Learning", "Educational", "Learn the letters.", "Easy", true));
        defaultGames.add(new Game("picture_match", "Picture Match", "Puzzle", "Identify animals, colors, fruits and objects.", "Easy", true));

        for (Game defaultGame : defaultGames) {
            String docId = defaultGame.getId();
            gameConfigRef.document(docId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot doc = task.getResult();
                    if (!doc.exists()) {
                        Map<String, Object> gameData = new HashMap<>();
                        gameData.put("gameId", defaultGame.getId());
                        gameData.put("gameName", defaultGame.getName());
                        gameData.put("category", defaultGame.getCategory());
                        gameData.put("description", defaultGame.getDescription());
                        gameData.put("difficulty", defaultGame.getDifficulty());
                        gameData.put("enabled", defaultGame.isEnabled());

                        gameConfigRef.document(docId).set(gameData)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Seeded game config: " + docId))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to seed game config: " + docId, e));
                    }
                }
            });
        }
    }

    private void listenToGameConfigs() {
        snapshotListener = gameConfigRef.addSnapshotListener((querySnapshots, error) -> {
            if (!isAdded()) return;

            if (error != null) {
                Log.e(TAG, "Error listening to gameConfig", error);
                Toast.makeText(requireContext(), "Error loading game status: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (querySnapshots != null) {
                List<Game> gameList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshots) {
                    try {
                        Game game = doc.toObject(Game.class);
                        if (game.getGameId() != null) {
                            game.setId(game.getGameId());
                        } else {
                            game.setId(doc.getId());
                        }
                        if (game.getName() == null || game.getName().isEmpty()) {
                            String nameFromDoc = doc.getString("gameName");
                            if (nameFromDoc != null) {
                                game.setGameName(nameFromDoc);
                            }
                        }
                        gameList.add(game);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing Game doc: " + doc.getId(), e);
                    }
                }

                // Sort games consistently according to the standard order
                sortGamesList(gameList);
                adapter.updateData(gameList);
            }
        });
    }

    private void sortGamesList(List<Game> games) {
        List<String> order = new ArrayList<>();
        order.add("math_quiz");
        order.add("word_builder");
        order.add("memory_match");
        order.add("alphabet_learning");
        order.add("picture_match");

        games.sort((g1, g2) -> {
            int pos1 = order.indexOf(g1.getId());
            int pos2 = order.indexOf(g2.getId());
            int val1 = pos1 != -1 ? pos1 : 99;
            int val2 = pos2 != -1 ? pos2 : 99;
            return Integer.compare(val1, val2);
        });
    }

    @Override
    public void onToggle(Game game, int position) {
        if (game == null || game.getId() == null) return;

        boolean newStatus = !game.isEnabled();
        gameConfigRef.document(game.getId())
                .update("enabled", newStatus)
                .addOnSuccessListener(aVoid -> {
                    if (!isAdded()) return;
                    String statusText = newStatus ? "Enabled" : "Disabled";
                    Snackbar.make(binding.getRoot(), game.getName() + " is now " + statusText, Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Failed to update " + game.getName() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
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
