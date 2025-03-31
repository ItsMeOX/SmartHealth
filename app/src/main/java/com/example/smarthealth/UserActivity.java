package com.example.smarthealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView fullNameText, emailText, mobileText, dobText, addressText, metricsText;
    private ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_user);

        profileService = RetrofitClient.getInstance().create(ProfileService.class);

        fullNameText = findViewById(R.id.fullNameText);
        emailText = findViewById(R.id.emailText);
        mobileText = findViewById(R.id.mobileText);
        dobText = findViewById(R.id.dobText);
        addressText = findViewById(R.id.addressText);
        metricsText = findViewById(R.id.metricsText);

        TextView editProfileBtn = findViewById(R.id.editProfile);
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView editMetricsBtn = findViewById(R.id.editMetricsBtn);
        editMetricsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, EditMetricsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        CardView signOutButton = findViewById(R.id.signOutBtn);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        long userId = sharedPreferences.getLong("user_id", 14);

        loadUserDetails(userId);
    }

    private void loadUserDetails(long userId){
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Call<UserDto> call = profileService.getUserById(userId);

        call.enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if(response.isSuccessful() && response.body() != null){
                    UserDto user = response.body();
                    fullNameText.setText(String.valueOf(user.getFullName()));
                    emailText.setText(String.valueOf(user.getEmail()));
                    mobileText.setText(String.valueOf(user.getPhoneNumber()));
                    dobText.setText(String.valueOf(user.getDob()));
                    addressText.setText(String.valueOf(user.getAddress()));
                    String weight = String.valueOf(user.getWeight());
                    String height = String.valueOf(user.getHeight());
                    metricsText.setText(weight + " kg | " + height + " cm" );
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Failed to load metrics", Toast.LENGTH_SHORT).show();
            }
        });
    }
}