package com.example.smarthealth;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<MedicineContainer> pillsContainers = new ArrayList<>();
    private ArrayList<MedicineContainer> liquidsContainers = new ArrayList<>();
    private ArrayList<MedicineContainer> othersContainers = new ArrayList<>();;
    private final int MAX_BUTTONS_PER_CONTAINER = 3; // Max buttons per row

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.inventory_medicine); // Your main layout


        LinearLayout pillsLayout = findViewById(R.id.pillsLinearLayout);
        LinearLayout liquidsLayout = findViewById(R.id.liquidsLinearLayout);
        LinearLayout othersLayout = findViewById(R.id.othersLinearLayout);

        ImageButton pillsButton = findViewById(R.id.fillByScan);
        ImageButton liquidsButton = findViewById(R.id.fillForm);
        ImageButton othersButton = findViewById(R.id.fillFormByHistory);

        pillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicineToLayout(pillsLayout, pillsContainers);
                Toast.makeText(MainActivity.this, "New Pill Added", Toast.LENGTH_SHORT).show();
            }
        });

        liquidsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicineToLayout(liquidsLayout, liquidsContainers);
                Toast.makeText(MainActivity.this, "New Liquid Added", Toast.LENGTH_SHORT).show();
            }
        });

        othersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMedicineToLayout(othersLayout, othersContainers);
                Toast.makeText(MainActivity.this, "New Other Medicine Added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMedicineToLayout(LinearLayout layout, ArrayList<MedicineContainer> containerList) {
        // Check if we need a new MedicineContainer
       if (containerList.isEmpty() || containerList.get(containerList.size() - 1).getButtonCount() >= MAX_BUTTONS_PER_CONTAINER) {
            // Create a new container using the existing LinearLayout
            MedicineContainer newContainer = new MedicineContainer(this);
            newContainer.classPointer = findViewById(R.id.pillsLinearLayout);
            layout.addView(newContainer.containerLayout);
            containerList.add(newContainer);
        }

        // Add a new button to the last container
        containerList.get(containerList.size() - 1).addMediButton(this);
    }
    }
