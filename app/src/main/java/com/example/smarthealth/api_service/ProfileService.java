package com.example.smarthealth.api_service;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProfileService {
    @GET("users/{id}")
    Call<UserDto> getUserById(@Path("id") long userId);

//    @PATCH("users/{id}/metrics")
//    Call<UserDto> updateMetrics(@Body ChangeMetricsRequest request);

    @PATCH("users/{id}/metrics")
    Call<UserDto> updateMetrics(
            @Path("id") long userId,
            @Query("weight") double weight,
            @Query("height") double height
    );

    @PATCH("users/{id}/profile")
    Call<UserDto> updateProfile(
            @Path("id") long userId,
            @Query("fullName") String fullName,
            @Query("dob") String dob,
            @Query("phoneNumber") String phoneNumber,
            @Query("address") String address
    );

    @Multipart
    @PATCH("users/{id}/profile-picture")
    Call<UserDto> uploadProfileImage(
            @Path("id") long userId,
            @Part MultipartBody.Part file // the name MUST match the backend's @RequestParam("file")
    );
}