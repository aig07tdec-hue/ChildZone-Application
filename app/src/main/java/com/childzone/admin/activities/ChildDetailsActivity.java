package com.childzone.admin.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.childzone.admin.R;
import com.childzone.admin.models.Child;

public class ChildDetailsActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailId, tvDetailScore, tvDetailRewards, tvDetailPlayTime, tvDetailProgress;
    private TextView tvDetailAge, tvDetailParentName, tvDetailParentEmail, tvDetailParentMobile, tvDetailCreatedAt;
    private TextView tvDetailEyeComfort, tvDetailUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initializeViews();

        Child child;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            child = getIntent().getSerializableExtra("child", Child.class);
        } else {
            child = (Child) getIntent().getSerializableExtra("child");
        }
        if (child != null) {
            displayChildDetails(child);
        }
    }

    private void initializeViews() {
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailId = findViewById(R.id.tvDetailId);
        tvDetailScore = findViewById(R.id.tvDetailScore);
        tvDetailRewards = findViewById(R.id.tvDetailRewards);
        tvDetailPlayTime = findViewById(R.id.tvDetailPlayTime);
        tvDetailProgress = findViewById(R.id.tvDetailProgress);
        tvDetailAge = findViewById(R.id.tvDetailAge);
        tvDetailParentName = findViewById(R.id.tvDetailParentName);
        tvDetailParentEmail = findViewById(R.id.tvDetailParentEmail);
        tvDetailParentMobile = findViewById(R.id.tvDetailParentMobile);
        tvDetailCreatedAt = findViewById(R.id.tvDetailCreatedAt);
        tvDetailEyeComfort = findViewById(R.id.tvDetailEyeComfort);
        tvDetailUid = findViewById(R.id.tvDetailUid);
    }

    private void displayChildDetails(Child child) {
        String name = child.getChildName();
        tvDetailName.setText(name != null && !name.trim().isEmpty() ? name : "Unnamed Child");

        String id = child.getChildId();
        tvDetailId.setText(getString(R.string.child_id_label, id != null ? id : "N/A"));

        tvDetailScore.setText(String.valueOf(child.getScore()));
        tvDetailRewards.setText(String.valueOf(child.getRewards()));
        tvDetailPlayTime.setText(child.getPlayTime() + "m");
        tvDetailProgress.setText(child.getProgress() + "%");

        String age = child.getAge();
        tvDetailAge.setText(age != null && !age.trim().isEmpty() ? age : "N/A");

        String pName = child.getParentName();
        tvDetailParentName.setText(pName != null && !pName.trim().isEmpty() ? pName : "N/A");

        String pEmail = child.getParentEmail();
        tvDetailParentEmail.setText(pEmail != null && !pEmail.trim().isEmpty() ? pEmail : "N/A");

        String pMobile = child.getParentMobile();
        tvDetailParentMobile.setText(pMobile != null && !pMobile.trim().isEmpty() ? pMobile : "N/A");

        String created = child.getCreatedAt();
        tvDetailCreatedAt.setText(created != null && !created.trim().isEmpty() ? created : "N/A");

        tvDetailEyeComfort.setText(child.isEyeComfortEnabled() ? "Enabled" : "Disabled");

        String uid = child.getFirebaseUid();
        tvDetailUid.setText(uid != null && !uid.trim().isEmpty() ? uid : "N/A");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}