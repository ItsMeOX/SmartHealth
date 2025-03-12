package com.example.smarthealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {

    private Spinner spinnerCountryCode;
    private List<CountryItem> countryList;
    private CountryAdapter countryAdapter;
    private TextInputEditText editTextMobile, editTextName, editTextEmail, editTextPassword;
    private TextView toLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextName = findViewById(R.id.editTextName);
        editTextName.setHint("John Doe");
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setHint("johndoe@gmail.com");
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.setHint("***");

        // Initialize country list
        countryList = new ArrayList<>();
        countryList.add(new CountryItem(R.drawable.malaysia, "+60"));
        countryList.add(new CountryItem(R.drawable.singapore, "+65"));
        countryList.add(new CountryItem(R.drawable.vietnam, "+84"));

        toLogIn = findViewById(R.id.toLogIn);
        toLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up adapter
        countryAdapter = new CountryAdapter(this, countryList);
        spinnerCountryCode.setAdapter(countryAdapter);

        // Set a listener
        spinnerCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CountryItem selectedItem = (CountryItem) parent.getItemAtPosition(position);
                updatePhoneHint(selectedItem.getCountryCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updatePhoneHint(String countryCode) {
        String hint;
        switch (countryCode) {
            case "+65": // Singapore
                hint = "6012 3456";
                break;
            case "+84": // Vietnam
                hint = "912 345 678";
                break;
            case "+60": // Malaysia
                hint = "12 345 6789";
                break;
            default:
                hint = "Phone Number";
                break;
        }
        editTextMobile.setHint(hint);
    }
}