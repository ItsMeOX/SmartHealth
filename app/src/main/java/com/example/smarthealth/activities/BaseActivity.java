package com.example.smarthealth.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthealth.MedicalCentreFinder.MapPageNavigation.MapClinicFinder;
import com.example.smarthealth.R;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    private Map<View, Integer> selectedIcons;
    private Map<View, Integer> unselectedIcons;
    private Map<View, View> iconViews;
    private Map<View, TextView> iconTexts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        preferences.edit().putLong("userId", 16).apply();
        preferences.edit().putBoolean("isLoggedIn", true).apply();

        if (!isLoggedIn) {
            // Redirect to LoginActivity
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.base_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        setNavbarListeners();
    }

    private void setNavbarListeners() {
        View navbarHomeLayout = findViewById(R.id.navbarHomeLayout);
        View navbarInventoryLayout = findViewById(R.id.navbarInventoryLayout);
        View navbarMedbotLayout = findViewById(R.id.navbarMedbotLayout);
        View navbarUserLayout = findViewById(R.id.navbarUserLayout);

        View navbarHomeIcon = findViewById(R.id.navbarHomeIcon);
        View navbarInventoryIcon = findViewById(R.id.navbarInventoryIcon);
        View navbarMedbotIcon = findViewById(R.id.navbarMedbotIcon);
        View navbarUserIcon = findViewById(R.id.navbarUserIcon);

        TextView navbarHomeText = findViewById(R.id.navbarHomeText);
        TextView navbarInventoryText = findViewById(R.id.navbarInventoryText);
        TextView navbarMedbotText = findViewById(R.id.navbarMedBotText);
        TextView navbarUserText = findViewById(R.id.navbarUserText);
        View navbarHospitalButton = findViewById(R.id.navbarHospitalBtn);

        iconViews = new HashMap<>();
        iconViews.put(navbarHomeLayout, navbarHomeIcon);
        iconViews.put(navbarInventoryLayout, navbarInventoryIcon);
        iconViews.put(navbarMedbotLayout, navbarMedbotIcon);
        iconViews.put(navbarUserLayout, navbarUserIcon);

        iconTexts = new HashMap<>();
        iconTexts.put(navbarHomeLayout, navbarHomeText);
        iconTexts.put(navbarInventoryLayout, navbarInventoryText);
        iconTexts.put(navbarMedbotLayout, navbarMedbotText);
        iconTexts.put(navbarUserLayout, navbarUserText);

        selectedIcons = new HashMap<>();
        selectedIcons.put(navbarHomeLayout, R.drawable.navbar_home_selected);
        selectedIcons.put(navbarInventoryLayout, R.drawable.navbar_inventory_selected);
        selectedIcons.put(navbarMedbotLayout, R.drawable.navbar_medbot_selected);
        selectedIcons.put(navbarUserLayout, R.drawable.navbar_user_selected);

        unselectedIcons = new HashMap<>();
        unselectedIcons.put(navbarHomeLayout, R.drawable.navbar_home);
        unselectedIcons.put(navbarInventoryLayout, R.drawable.navbar_inventory);
        unselectedIcons.put(navbarMedbotLayout, R.drawable.navbar_medbot);
        unselectedIcons.put(navbarUserLayout, R.drawable.navbar_user);

        navbarHomeLayout.setOnClickListener(v -> handleNavbarClick(navbarHomeLayout, new HomeFragment()));
        navbarInventoryLayout.setOnClickListener(v -> handleNavbarClick(navbarInventoryLayout, new HomeFragment()));
        navbarMedbotLayout.setOnClickListener(v -> handleNavbarClick(navbarMedbotLayout, new HomeFragment()));
        navbarUserLayout.setOnClickListener(v -> handleNavbarClick(navbarUserLayout, new UserFragment()));
        navbarHospitalButton.setOnClickListener(view -> {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!lm.isLocationEnabled()) {
                    Toast.makeText(this, "Please Enable Location Services First", Toast.LENGTH_SHORT).show();
                } else {
                    Intent startMapClinicFinder = new Intent(BaseActivity.this, MapClinicFinder.class);
                    startActivity(startMapClinicFinder);
                }
            }
        });
    }

    private void handleNavbarClick(View selectedLayout, Fragment fragment) {
        for (Map.Entry<View, View> entry : iconViews.entrySet()) {
            View layout = entry.getKey();
            View iconView = entry.getValue();
            TextView textView = iconTexts.get(layout);
            iconView.setBackgroundResource(unselectedIcons.get(layout));
            textView.setTextColor(getColor(R.color.lightgray));
        }

        View selectedIcon = iconViews.get(selectedLayout);
        selectedIcon.setBackgroundResource(selectedIcons.get(selectedLayout));

        TextView textView = iconTexts.get(selectedLayout);
        textView.setTextColor(getColor(R.color.primary_orange));

        loadFragment(fragment);
    }
    // TODO 1: highlight current page icon.
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}