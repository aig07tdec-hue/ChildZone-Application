package com.childzone.admin.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.childzone.admin.R;
import com.childzone.admin.databinding.ActivityAdminDashboardBinding;
import com.childzone.admin.fragments.ChildManagementFragment;
import com.childzone.admin.fragments.DashboardFragment;
import com.childzone.admin.fragments.GameManagementFragment;
import com.childzone.admin.fragments.ReportsFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityAdminDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        setSupportActionBar(binding.toolbar);

        // Setup Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup Navigation View
        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.setCheckedItem(R.id.nav_dashboard);

        // Handle Back Press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        setupBottomNavigation();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_dashboard);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            Fragment fragment = null;
            if (id == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
            } else if (id == R.id.nav_children) {
                fragment = new ChildManagementFragment();
            } else if (id == R.id.nav_games) {
                fragment = new GameManagementFragment();
            } else if (id == R.id.nav_reports) {
                fragment = new ReportsFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        // Update Toolbar title based on fragment
        updateToolbarTitle(fragment);
    }

    private void updateToolbarTitle(Fragment fragment) {
        if (getSupportActionBar() == null) return;

        if (fragment instanceof DashboardFragment) {
            getSupportActionBar().setTitle("Dashboard");
        } else if (fragment instanceof ChildManagementFragment) {
            getSupportActionBar().setTitle("Children");
        } else if (fragment instanceof GameManagementFragment) {
            getSupportActionBar().setTitle("Games");
        } else if (fragment instanceof ReportsFragment) {
            getSupportActionBar().setTitle("Reports");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        binding.bottomNavigation.setSelectedItemId(id);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }
}