package com.example.smarthealth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
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
}