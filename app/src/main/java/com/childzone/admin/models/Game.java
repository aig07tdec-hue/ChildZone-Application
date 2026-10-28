package com.childzone.admin.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Game {
    private String id;
    private String gameId;
    private String gameName;
    private String category;
    private String description;
    private String difficulty;
    private boolean enabled;

    public Game() {
    }

    public Game(String gameId, String gameName, String category, String description, String difficulty, boolean enabled) {
        this.id = gameId;
        this.gameId = gameId;
        this.gameName = gameName;
        this.category = category;
        this.description = description;
        this.difficulty = difficulty;
        this.enabled = enabled;
    }

    public String getId() {
        return id != null ? id : gameId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return gameName != null ? gameName : "";
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
