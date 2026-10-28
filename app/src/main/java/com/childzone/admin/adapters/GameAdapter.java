package com.childzone.admin.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.childzone.admin.R;
import com.childzone.admin.models.Game;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    public interface OnGameToggleListener {
        void onToggle(Game game, int position);
    }

    private List<Game> gameList;
    private OnGameToggleListener toggleListener;

    public GameAdapter(List<Game> gameList, OnGameToggleListener toggleListener) {
        this.gameList = gameList;
        this.toggleListener = toggleListener;
    }

    public void updateData(List<Game> newGames) {
        this.gameList = newGames;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);
        holder.tvGameName.setText(game.getName());
        holder.tvGameCategory.setText("Category: " + (game.getCategory() != null ? game.getCategory() : "General"));
        holder.tvGameDifficulty.setText("Difficulty: " + (game.getDifficulty() != null ? game.getDifficulty() : "Easy"));

        if (game.isEnabled()) {
            holder.tvGameStatus.setText("Enabled");
            holder.tvGameStatus.getBackground().mutate().setTint(Color.parseColor("#4CAF50"));
            holder.btnToggleGame.setText("Disable");
            holder.btnToggleGame.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
        } else {
            holder.tvGameStatus.setText("Disabled");
            holder.tvGameStatus.getBackground().mutate().setTint(Color.parseColor("#F44336"));
            holder.btnToggleGame.setText("Enable");
            holder.btnToggleGame.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }

        holder.btnToggleGame.setOnClickListener(v -> {
            if (toggleListener != null) {
                toggleListener.onToggle(game, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList != null ? gameList.size() : 0;
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView tvGameName, tvGameCategory, tvGameDifficulty, tvGameStatus;
        MaterialButton btnToggleGame;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameName = itemView.findViewById(R.id.tvGameName);
            tvGameCategory = itemView.findViewById(R.id.tvGameCategory);
            tvGameDifficulty = itemView.findViewById(R.id.tvGameDifficulty);
            tvGameStatus = itemView.findViewById(R.id.tvGameStatus);
            btnToggleGame = itemView.findViewById(R.id.btnToggleGame);
        }
    }
}
