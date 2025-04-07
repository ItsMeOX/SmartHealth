package com.example.smarthealth.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
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
import com.example.smarthealth.api_service.LoginRequest;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.country_code.CountryAdapter;
import com.example.smarthealth.country_code.CountryItem;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;
    private ImageView loginButton;
    private Spinner spinnerCountryCode;
    private List<CountryItem> countryList;
    private CountryAdapter countryAdapter;
    private FloatingActionButton googleButton;
    private final CredentialManager credentialManager = CredentialManager.create(this);
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        authService = RetrofitClient.getInstance().create(AuthService.class);

        loginButton = findViewById(R.id.cirLoginButton);
        googleButton = findViewById(R.id.btnGoogleLogin);

        editTextEmail = findViewById(R.id.editLoginTextEmail);
        editTextEmail.setHint("johndoe@gmail.com");

        editTextPassword = findViewById(R.id.editLoginTextPassword);
        editTextPassword.setHint("*****");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please enter your email or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(email, password);
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

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateGoogleSignIn();
            }
        });

    }

    private void loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        Call<AuthResponse> call = authService.loginUser(request);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // Save login state
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putLong("userId", response.body().getId());
                    editor.apply();

                    // Navigate to MainActivity
                    Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
                    Toast.makeText(LoginActivity.this, "Google Login Successful!", Toast.LENGTH_SHORT).show();
                    // Save login state and proceed to main activity
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putLong("userId", response.body().getId());
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Google authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleGoogleSignInFailure(GetCredentialException e) {
        Log.e(TAG, "Google Sign-In failed", e);
        Toast.makeText(this, "Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
