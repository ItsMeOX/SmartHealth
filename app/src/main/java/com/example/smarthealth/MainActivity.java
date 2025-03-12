package com.example.smarthealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        // Check if the user is logged in
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // Redirect to LoginActivity
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Stop further execution of onCreate
        }

        setContentView(R.layout.activity_main);

//        View mainContentView = findViewById(R.id.main_content_view);
//        mainContentView.setOnTouchListener(new View.OnTouchListener() {
//            private float dY;
//            private float originalY = -1;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        if (originalY == -1) {
//                            mainContentView.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    originalY = mainContentView.getY();
//                                }
//                            });
//                        }
//                        dY = v.getY() - event.getRawY();
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        float newY = event.getRawY() + dY;
//                        if (newY >= originalY) {
//                            v.setY(newY);
//                        }
//                        return true;
//                }
//                return false;
//            }
//        });

        Button logoutButton = findViewById(R.id.navbar_medbot_btn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void logout() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
