package com.example.smarthealth.api_service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("/api/auth/google-auth")
    Call<AuthResponse> googleAuth(@Body GoogleAuthRequest request);

    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegistrationRequest request);
}

