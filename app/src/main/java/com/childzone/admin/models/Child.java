package com.childzone.admin.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

@IgnoreExtraProperties
public class Child implements Serializable {
    private String childId;
    private String childName;
    private String age;
    private String parentName;
    private String parentMobile;
    private String parentEmail;
    private String firebaseUid;
    private String parentPinHash;
    private String createdAt;
    private int score;
    private int progress;
    private int playTime;
    private int rewards;
    private boolean eyeComfortEnabled;

    public Child() {
        // Required for Firestore
    }

    public Child(String childId, String childName, String age, String parentName, String parentMobile, String parentEmail, String firebaseUid, String parentPinHash, String createdAt, int score, int progress, int playTime, int rewards, boolean eyeComfortEnabled) {
        this.childId = childId;
        this.childName = childName;
        this.age = age;
        this.parentName = parentName;
        this.parentMobile = parentMobile;
        this.parentEmail = parentEmail;
        this.firebaseUid = firebaseUid;
        this.parentPinHash = parentPinHash;
        this.createdAt = createdAt;
        this.score = score;
        this.progress = progress;
        this.playTime = playTime;
        this.rewards = rewards;
        this.eyeComfortEnabled = eyeComfortEnabled;
    }

    public static Child fromDocument(DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) return null;

        Child child = new Child();

        // childId
        String docId = doc.getId();
        Object cIdObj = doc.get("childId");
        String cId = cIdObj != null ? String.valueOf(cIdObj) : null;
        child.setChildId(cId != null && !cId.trim().isEmpty() ? cId : docId);

        // childName
        Object cNameObj = doc.get("childName");
        if (cNameObj == null) {
            cNameObj = doc.get("name");
        }
        String cName = cNameObj != null ? String.valueOf(cNameObj) : null;
        child.setChildName(cName != null && !cName.trim().isEmpty() ? cName : "Unnamed Child");

        // age
        Object ageObj = doc.get("age");
        child.setAge(ageObj != null ? String.valueOf(ageObj) : "N/A");

        // parent details
        Object pNameObj = doc.get("parentName");
        child.setParentName(pNameObj != null ? String.valueOf(pNameObj) : "N/A");

        Object pEmailObj = doc.get("parentEmail");
        child.setParentEmail(pEmailObj != null ? String.valueOf(pEmailObj) : "N/A");

        Object pMobileObj = doc.get("parentMobile");
        child.setParentMobile(pMobileObj != null ? String.valueOf(pMobileObj) : "N/A");

        // stats & activity
        Object scoreObj = doc.get("score");
        if (scoreObj instanceof Number) {
            child.setScore(((Number) scoreObj).intValue());
        } else {
            child.setScore(0);
        }

        Object progressObj = doc.get("progress");
        if (progressObj instanceof Number) {
            child.setProgress(((Number) progressObj).intValue());
        } else {
            child.setProgress(0);
        }

        Object playTimeObj = doc.get("playTime");
        if (playTimeObj instanceof Number) {
            child.setPlayTime(((Number) playTimeObj).intValue());
        } else {
            child.setPlayTime(0);
        }

        Object rewardsObj = doc.get("rewards");
        if (rewardsObj instanceof Number) {
            child.setRewards(((Number) rewardsObj).intValue());
        } else {
            child.setRewards(0);
        }

        Object eyeComfortObj = doc.get("eyeComfortEnabled");
        child.setEyeComfortEnabled(Boolean.TRUE.equals(eyeComfortObj));

        Object uidObj = doc.get("firebaseUid");
        child.setFirebaseUid(uidObj != null ? String.valueOf(uidObj) : "");

        // createdAt - can be Timestamp or String
        Object createdObj = doc.get("createdAt");
        if (createdObj instanceof Timestamp) {
            Timestamp ts = (Timestamp) createdObj;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            child.setCreatedAt(sdf.format(ts.toDate()));
        } else if (createdObj != null) {
            child.setCreatedAt(String.valueOf(createdObj));
        } else {
            child.setCreatedAt("N/A");
        }

        return child;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentMobile() {
        return parentMobile;
    }

    public void setParentMobile(String parentMobile) {
        this.parentMobile = parentMobile;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getParentPinHash() {
        return parentPinHash;
    }

    public void setParentPinHash(String parentPinHash) {
        this.parentPinHash = parentPinHash;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public int getRewards() {
        return rewards;
    }

    public void setRewards(int rewards) {
        this.rewards = rewards;
    }

    public boolean isEyeComfortEnabled() {
        return eyeComfortEnabled;
    }

    public void setEyeComfortEnabled(boolean eyeComfortEnabled) {
        this.eyeComfortEnabled = eyeComfortEnabled;
    }
}
