package com.example.smarthealth.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthealth.R;

abstract public class BaseActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FrameLayout container = findViewById(R.id.activityContainer);
        View childView = getLayoutInflater().inflate(getContentLayoutId(), container, false);
        container.addView(childView);

        setNavbarListeners();
    }

    // To be overriden by child class to attach navbar
    abstract protected int getContentLayoutId();

    private void setNavbarListeners() {
        View navbarHomeLayout = findViewById(R.id.navbarHomeLayout);
        View navbarInventoryLayout = findViewById(R.id.navbarInventoryLayout);
        View navbarMedbotLayout = findViewById(R.id.navbarMedbotLayout);
        View navbarUserLayout = findViewById(R.id.navbarUserLayout);

        navbarHomeLayout.setOnClickListener(v -> navigateToHome());
        navbarInventoryLayout.setOnClickListener(v -> navigateToInventory());
        navbarMedbotLayout.setOnClickListener(v -> navigateToMedbot());
        navbarUserLayout.setOnClickListener(v -> navigateToUser());
    }

    // TODO 1: consider using fragment instead of new activity for smoother transition between pages.
    // TODO 2: highlight current page icon.

    private void navigateToHome() {
        startActivity(new Intent(BaseActivity.this, MainActivity.class));
    }

    private void navigateToInventory() {

    }

    private void navigateToMedbot() {

    }

    private void navigateToUser() {

    }
}
