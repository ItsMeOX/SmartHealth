package com.example.smarthealth.activities;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthealth.R;
import com.example.smarthealth.api_service.ProfileService;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.api_service.UserDto;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMetricsFragment extends Fragment {

    private CardView saveMetricsBtn;
    private TextInputEditText editHeight, editWeight;
    private TextView tvBmi;
    private SharedPreferences sharedPreferences;
    private ProfileService profileService;

    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceBundle);
//        setContentView(R.layout.edit_metrics_fragment);
        view = inflater.inflate(R.layout.edit_metrics_fragment, container, false);

        profileService = RetrofitClient.getInstance().create(ProfileService.class);

        editHeight = view.findViewById(R.id.editHeight);
        editWeight = view.findViewById(R.id.editWeight);
        tvBmi = view.findViewById(R.id.tvBmi);

        saveMetricsBtn = view.findViewById(R.id.saveMetricsButton);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        long userId = sharedPreferences.getLong("userId", -1);
        Log.d("User_ID", "current User_ID: " + userId);

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

        return view;
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
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserDto> call = profileService.updateMetrics(userId, weight, height);

        call.enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Update Metrics Successful!", Toast.LENGTH_SHORT).show();
                    Fragment userFragment = new UserFragment();
                    FragmentTransaction transaction = getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction();
                    transaction.replace(R.id.fragmentContainer, userFragment);
                    transaction.commit();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(getActivity(), "Update Metrics Failed: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "Update Metrics Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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