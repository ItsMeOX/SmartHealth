package com.example.smarthealth;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
public class AddNewMedicineButton {
    private Context context;
    private LinearLayout parentLayout;

    public AddNewMedicineButton(Context context, LinearLayout parentLayout) {
        this.context = context;
        this.parentLayout = parentLayout;
    }

    public void addButton(String medicineName) {
        Button newButton = new Button(context);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Full width
                LinearLayout.LayoutParams.WRAP_CONTENT // Wrap height
        );
        buttonParams.setMargins(16, 8, 8, 16); // Set margins for spacing

        newButton.setLayoutParams(buttonParams);
        newButton.setText(medicineName);
        newButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_light));

        newButton.setOnClickListener(v -> {
            Toast.makeText(context, "Medicine Selected: " + medicineName, Toast.LENGTH_SHORT).show();
        });

        // Add the button to the parent layout
        parentLayout.addView(newButton);
    }
}
