package com.example.smarthealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    private CardView saveProfileBtn;
    private TextInputEditText editFullName, editDOB, editMobile, editAddress;
    private SharedPreferences sharedPreferences;
    private ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_edit_profile);

        profileService = RetrofitClient.getInstance().create(ProfileService.class);

        editFullName = findViewById(R.id.editFullName);
        editDOB = findViewById(R.id.editDOB);
        editMobile = findViewById(R.id.editMobile);
        editAddress = findViewById(R.id.editAddress);

        saveProfileBtn = findViewById(R.id.saveProfileButton);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        long userId = sharedPreferences.getLong("user_id", 14);

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(userId);
            }
        });
    }

    private void updateProfile(long userId){
        String fullName = editFullName.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        if(!validateInputs(fullName, dob, mobile, address)){
            return;
        }

        if (fullName.isEmpty() || dob.isEmpty() || mobile.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserDto> call = profileService.updateProfile(userId, fullName, dob, mobile, address);

        call.enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Update Profile Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfileActivity.this, UserActivity.class));
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(EditProfileActivity.this, "Update Profile Failed: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(EditProfileActivity.this, "Update Profile Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs(String fullName, String dob, String mobile, String address) {
        if (fullName.isEmpty()) {
            editFullName.setError("Name is required");
            return false;
        }

        if (!validateDOB(dob)) {
            return false;
        }

        if (mobile.isEmpty()) {
            editMobile.setError("Phone number is required");
            return false;
        }

        if (address.isEmpty()) {
            editAddress.setError("Phone number is required");
            return false;
        }

        return true;
    }
    private boolean validateDOB(String dobStr) {
        if (dobStr.isEmpty()) {
            editDOB.setError("Date of Birth cannot be empty");
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        sdf.setLenient(false); // Ensures strict date parsing

        try {
            Date dob = sdf.parse(dobStr);
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dob);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);

            // Adjust age if the birth date has not occurred this year yet
            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            if (age < 13) {
                editDOB.setError("You must be at least 13 years old");
                return false;
            }

            if (age > 120) {
                editDOB.setError("Please enter a valid age");
                return false;
            }

        } catch (ParseException e) {
            editDOB.setError("Enter a valid date (dd/MM/yyyy)");
            return false;
        }

        return true;
    }
}