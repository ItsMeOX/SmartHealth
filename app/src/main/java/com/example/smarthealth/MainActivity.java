package com.example.smarthealth;


import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//New additions:
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    Button clinicNavButton;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_main);

        View mainContentView = findViewById(R.id.main_content_view);
        mainContentView.setOnTouchListener(new View.OnTouchListener() {
            private float dY;
            private float originalY = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (originalY == -1) {
                            mainContentView.post(new Runnable() {
                                @Override
                                public void run() {
                                    originalY = mainContentView.getY();
                                }
                            });
                        }

                        dY = v.getY() - event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float newY = event.getRawY() + dY;
                        if (newY >= originalY) {
                            v.setY(newY);
                        }
                        return true;
                }
                return false;
            }
        });

        //additions:
        clinicNavButton = (Button)findViewById(R.id.navbar_medbot_btn);
        clinicNavButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,MapClinicFinder.class);
                startActivity(intent);
                finish();
            }
        });

    }
}