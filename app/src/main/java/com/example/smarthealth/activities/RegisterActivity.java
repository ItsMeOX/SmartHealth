package com.example.smarthealth.activities;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.GetPasswordOption;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.smarthealth.R;
import com.example.smarthealth.api_service.AuthResponse;
import com.example.smarthealth.api_service.AuthService;
import com.example.smarthealth.api_service.GoogleAuthRequest;
import com.example.smarthealth.api_service.RegistrationRequest;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.api_service.UpcomingScheduleService;
import com.example.smarthealth.country_code.CountryAdapter;
import com.example.smarthealth.country_code.CountryItem;
import com.example.smarthealth.nutrient_intake.DatabaseNutrientIntakeProvider;
import com.example.smarthealth.nutrient_intake.NutrientIntakeProvider;
import com.example.smarthealth.upcoming_schedule.DatabaseUpcomingScheduleProvider;
import com.example.smarthealth.upcoming_schedule.UpcomingScheduleProvider;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Spinner spinnerCountryCode;
    private List<CountryItem> countryList;
    private CountryAdapter countryAdapter;
    private TextInputEditText editTextMobile, editTextName, editTextEmail, editTextPassword;
    private TextView toLogIn;
    private ImageView cirRegisterButton;
    private FloatingActionButton googleButton;
    private final CredentialManager credentialManager = CredentialManager.create(this);
    private AuthService authService;
    private NutrientIntakeProvider nutrientIntakeProvider;
    private UpcomingScheduleProvider upcomingScheduleProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        authService = RetrofitClient.getInstance().create(AuthService.class);
        upcomingScheduleProvider = new DatabaseUpcomingScheduleProvider();
        nutrientIntakeProvider = new DatabaseNutrientIntakeProvider();

        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextName = findViewById(R.id.editTextName);
        editTextName.setHint("John Doe");
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setHint("johndoe@gmail.com");
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.setHint("*****");

        toLogIn = findViewById(R.id.toLogIn);
        googleButton = findViewById(R.id.btnGoogleLogin);

        toLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cirRegisterButton = findViewById(R.id.cirRegisterButton);
        cirRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

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
                updatePhoneHint(selectedItem.getCountryCode());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateGoogleSignIn();
            }
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

    private void registerUser() {
        String fullName = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String countryCode = ((CountryItem)spinnerCountryCode.getSelectedItem()).getCountryCode();
        String phoneNumber = countryCode + editTextMobile.getText().toString().trim();

        if (!validateInputs(fullName, email, password, phoneNumber)) {
            return;
        }

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RegistrationRequest request = new RegistrationRequest(fullName, email, password, phoneNumber);
        Call<AuthResponse> call = authService.registerUser(request);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    nutrientIntakeProvider.initializeNutrientIntake(response.body().getId(), new DatabaseNutrientIntakeProvider.OnIntakeAddedCallback() {
                        @Override
                        public void onIntakeAdded(boolean success) {
                            if(success) {
                                Toast.makeText(RegisterActivity.this, "Initialize Data 1 Successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to initialize nutrient data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    upcomingScheduleProvider.initializeUpcomingSchedule(response.body().getId(), new DatabaseUpcomingScheduleProvider.OnUpcomingScheduleAddedCallback() {
                        @Override
                        public void onUpcomingScheduleAdded(boolean success) {
                            if(success) {
                                Toast.makeText(RegisterActivity.this, "Initialize Data 2 Successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to initialize upcoming schedule data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs(String fullName, String email, String password, String phoneNumber) {
        if (fullName.isEmpty()) {
            editTextName.setError("Name is required");
            return false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter valid email");
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (phoneNumber.isEmpty()) {
            editTextMobile.setError("Phone number is required");
            return false;
        }

        return true;
    }

    private void initiateGoogleSignIn() {
        try {
            // Generate a nonce
            String rawNonce = UUID.randomUUID().toString();
            String hashedNonce = hashNonce(rawNonce);
            GetPasswordOption getPasswordOption = new GetPasswordOption();

            // Configure Google ID token request
            GetGoogleIdOption googleIdOption =
                    new GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId("172027414357-vnjmcenkdjqhu2lsuq654u5d9s6jfsos.apps.googleusercontent.com")
                            .setNonce(hashedNonce)
                            .build();

            // Create Credential Request
            GetCredentialRequest request = new GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .addCredentialOption(getPasswordOption)
                    .build();

            // Perform credential request asynchronously
            credentialManager.getCredentialAsync(
                    this,
                    request,
                    new CancellationSignal(),
                    Executors.newSingleThreadExecutor(),
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            handleGoogleSignInSuccess(result);
                        }

                        @Override
                        public void onError(GetCredentialException e) {
                            handleGoogleSignInFailure(e);
                        }
                    }
            );

        } catch (Exception e) {
            Log.d("GoogleSignIn", "Context: " + this);
            Log.e(TAG, "Error initiating Google Sign-In: ", e);
            Toast.makeText(this, "Error initiating Google Sign-In", Toast.LENGTH_SHORT).show();
        }
    }

    private String hashNonce(String rawNonce) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(rawNonce.getBytes(StandardCharsets.UTF_8));
        StringBuilder hashedNonce = new StringBuilder();
        for (byte b : digest) {
            hashedNonce.append(String.format("%02x", b));
        }
        return hashedNonce.toString();
    }

    private void handleGoogleSignInSuccess(GetCredentialResponse result) {
        Credential credential = result.getCredential();
        if (credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            GoogleIdTokenCredential googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.getData());
            String googleIdToken = googleIdTokenCredential.getIdToken();
            String email = googleIdTokenCredential.getId();

            authenticateWithBackend(email, googleIdToken);
        }
    }

    private void authenticateWithBackend(String email, String idToken) {
        GoogleAuthRequest request = new GoogleAuthRequest(email, idToken);
        Call<AuthResponse> call = authService.googleAuth(request);

        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Google Login Successful!", Toast.LENGTH_SHORT).show();
                    // Save login state and proceed to main activity
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putLong("userId", response.body().getId());
                    editor.apply();

                    Intent intent = new Intent(RegisterActivity.this, BaseActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Google authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleGoogleSignInFailure(GetCredentialException e) {
        Log.e(TAG, "Google Sign-In failed", e);
        Toast.makeText(this, "Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}