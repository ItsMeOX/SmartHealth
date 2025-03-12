package com.example.smarthealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextMobile, editTextPassword;
    private ImageView loginButton;
    private Spinner spinnerCountryCode;
    private List<CountryItem> countryList;
    private CountryAdapter countryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.cirLoginButton);

        editTextMobile = findViewById(R.id.editLoginTextMobile);
        editTextMobile.setHint("Enter your phone number");

        editTextPassword = findViewById(R.id.editLoginTextPassword);
        editTextPassword.setHint("*****");


        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        // Initialize country list
        countryList = new ArrayList<>();
        countryList.add(new CountryItem(R.drawable.malaysia, "+60"));
        countryList.add(new CountryItem(R.drawable.singapore, "+65"));
        countryList.add(new CountryItem(R.drawable.vietnam, "+84"));
        // Set up adapter
        countryAdapter = new CountryAdapter(this, countryList);
        spinnerCountryCode.setAdapter(countryAdapter);

        // Set a listener
        spinnerCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CountryItem selectedItem = (CountryItem) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simulate a successful login
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                // Navigate to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Prevent going back to LoginActivity
            }
        });

        TextView toRegister = findViewById(R.id.toRegister);
        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
