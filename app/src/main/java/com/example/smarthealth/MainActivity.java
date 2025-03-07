package com.example.smarthealth;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout pillLayout;

    public void AddNewMedicineButton(View view) {
        // Here you can handle what happens when the button is clicked
        // For example, adding a new medicine button
        AddNewMedicineButton buttonManager = new AddNewMedicineButton(this, findViewById(R.id.pill1)); // Replace with your layout ID
        buttonManager.addButton("Paracetamol");
        buttonManager.addButton("Ibuprofen");

        // Optional: Show a toast or log message to confirm the button click
        Toast.makeText(this, "Add New Medicine Button clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_medicine); // Your layout

        LinearLayout pillLayout = findViewById(R.id.pill1); // Reference to the layout where buttons will be added

        // Create button manager and add buttons
        AddNewMedicineButton buttonManager = new AddNewMedicineButton(this, pillLayout);
        buttonManager.addButton("Paracetamol");
        buttonManager.addButton("Ibuprofen");

        // Handling the drag gesture for the mainContentView
        View mainContentView = findViewById(R.id.main_content_view);
        mainContentView.setOnTouchListener(new View.OnTouchListener() {
            private float dY;
            private float originalY = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (originalY == -1) {
                            mainContentView.post(() -> originalY = mainContentView.getY());
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
    }
}

