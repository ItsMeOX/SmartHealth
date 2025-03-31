package com.example.smarthealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditMetricsActivity extends AppCompatActivity {

    private CardView saveMetricsBtn;
    private TextInputEditText editHeight, editWeight;
    private TextView tvBmi;
    private SharedPreferences sharedPreferences;
    private ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_edit_metrics_only);

        profileService = RetrofitClient.getInstance().create(ProfileService.class);

        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        tvBmi = findViewById(R.id.tvBmi);

        saveMetricsBtn = findViewById(R.id.saveMetricsButton);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        long userId = sharedPreferences.getLong("user_id", 14);

        // Add TextWatchers to update BMI in real-time
        TextWatcher bmiTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAndDisplayBMI(); // Call BMI function when user types
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editHeight.addTextChangedListener(bmiTextWatcher);
        editWeight.addTextChangedListener(bmiTextWatcher);

        saveMetricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMetrics(userId);
            }
        });
    }

    private void calculateAndDisplayBMI() {
        String weightStr = editWeight.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();

        if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightStr);
                double height = Double.parseDouble(heightStr) / 100;

                if (weight > 0 && height > 0) {
                    double bmi = weight / (height * height);
                    tvBmi.setText(String.format("BMI: %.2f", bmi));

                    if (bmi < 18.5) {
                        tvBmi.setTextColor(getResources().getColor(R.color.blue));
                    } else if (bmi >= 18.5 && bmi < 24.9) {
                        tvBmi.setTextColor(getResources().getColor(R.color.green));
                    } else if (bmi >= 25 && bmi < 29.9) {
                        tvBmi.setTextColor(getResources().getColor(R.color.orange)); // Overweight (orange)
                    } else {
                        tvBmi.setTextColor(getResources().getColor(R.color.red)); // Obese (red)
                    }
                } else {
                    tvBmi.setText("BMI: -");
                    tvBmi.setTextColor(getResources().getColor(R.color.gray));
                }
            } catch (NumberFormatException e) {
                tvBmi.setText("BMI: -");
                tvBmi.setTextColor(getResources().getColor(R.color.gray));
            }
        } else {
            tvBmi.setText("BMI: -");
            tvBmi.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    private void updateMetrics(long userId){
        String weightStr = editWeight.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();

        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);

        if(!validateInputs(weightStr, heightStr)){
            return;
        }

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserDto> call = profileService.updateMetrics(userId, weight, height);

        call.enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditMetricsActivity.this, "Update Metrics Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditMetricsActivity.this, UserActivity.class));
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(EditMetricsActivity.this, "Update Metrics Failed: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(EditMetricsActivity.this, "Update Metrics Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(EditMetricsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs(String weightStr, String heightStr) {
        if (weightStr.isEmpty()) {
            editWeight.setError("Weight cannot be empty");
            return false;
        }
        if (heightStr.isEmpty()) {
            editHeight.setError("Height cannot be empty");
            return false;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            if (weight <= 0) {
                editWeight.setError("Weight must be greater than 0");
                return false;
            }

            if (height <= 0) {
                editHeight.setError("Height must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            editWeight.setError("Invalid number format");
            editHeight.setError("Invalid number format");
            return false;
        }

        return true;
    }
}