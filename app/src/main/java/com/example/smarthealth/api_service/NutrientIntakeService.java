package com.example.smarthealth.api_service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NutrientIntakeService {
    @POST("nutrient-intake/{userId}")
    Call<NutrientIntakeDto> addNutrientIntake(
            @Path("userId") long userId,
            @Body NutrientIntakeDto nutrientIntakeDto
    );

    @GET("nutrient-intake/{userId}")
    Call<List<NutrientIntakeDto>> getUserDailyEvents(
            @Path("userId") long userId
    );

    @POST("nutrient-intake/reset")
    Call<String> resetDailyNutrientIntakes();

    @GET("nutrient-intake/{userId}/exists")
    Call<Boolean> checkIfUserHasIntakes(
            @Path("userId") long userId
    );

    @PATCH("nutrient-intake/{intakeId}")
    Call<NutrientIntakeDto> updateNutrientIntake(
            @Path("intakeId") long intakeId,
            @Body NutrientIntakeDto nutrientIntakeDto
    );
}
