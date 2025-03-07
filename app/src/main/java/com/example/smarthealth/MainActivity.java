package com.example.smarthealth;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_main);

        View mainContentView = findViewById(R.id.mainContentView);
        mainContentView.setOnTouchListener(new View.OnTouchListener() {
            private float dY;
            private float originalY = -1;
            final private float moveThreshY = 100;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getRawY() - mainContentView.getY() <= moveThreshY) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }

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
                        if (event.getRawY() - mainContentView.getY() > moveThreshY) {
                            return true;
                        }
                        float newY = event.getRawY() + dY;
                        if (newY >= originalY) {
                            v.setY(newY);
                        }
                        return true;
                }

                return false;
            }
        });
    }
}
