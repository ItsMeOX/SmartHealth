package com.example.smarthealth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

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
        Button navbarHomeBtn = findViewById(R.id.navbarHomeBtn);
        Button navbarInventoryBtn = findViewById(R.id.navbarInventoryBtn);
        Button navbarMedbotBtn = findViewById(R.id.navbarMedbotBtn);
        Button navbarUserBtn = findViewById(R.id.navbarUserBtn);

        navbarHomeBtn.setOnClickListener(v -> navigateToHome());
        navbarInventoryBtn.setOnClickListener(v -> navigateToInventory());
        navbarMedbotBtn.setOnClickListener(v -> navigateToMedbot());
        navbarUserBtn.setOnClickListener(v -> navigateToUser());
    }

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
