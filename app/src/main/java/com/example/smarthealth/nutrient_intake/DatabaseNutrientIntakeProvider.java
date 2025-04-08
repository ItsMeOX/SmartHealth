package com.example.smarthealth.nutrient_intake;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.smarthealth.R;
import com.example.smarthealth.activities.LoginActivity;
import com.example.smarthealth.activities.RegisterActivity;
import com.example.smarthealth.api_service.AuthResponse;
import com.example.smarthealth.api_service.NutrientIntakeDto;
import com.example.smarthealth.api_service.NutrientIntakeService;
import com.example.smarthealth.api_service.RegistrationRequest;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.country_code.CountryItem;
import com.example.smarthealth.nutrient_intake.units.MassUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseNutrientIntakeProvider implements NutrientIntakeProvider {
    private NutrientIntakeService nutrientIntakeService;
    public DatabaseNutrientIntakeProvider() {
        nutrientIntakeService = RetrofitClient.getInstance().create(NutrientIntakeService.class);
    }

    @Override
    public List<NutrientIntake> getNutrientIntakes(long userId, OnDataLoadedCallback callback) {
        List<NutrientIntake> res = new ArrayList<>();

        Call<List<NutrientIntakeDto>> call = nutrientIntakeService.getUserDailyEvents(userId);

        call.enqueue(new Callback<List<NutrientIntakeDto>>() {
            @Override
            public void onResponse(Call<List<NutrientIntakeDto>> call, Response<List<NutrientIntakeDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for (NutrientIntakeDto nutrientIntakeDto : response.body()) {
                        int nutrientIconId = 0;
                        if(nutrientIntakeDto.getNutrientName().equals("Carbs")){
                            nutrientIconId = R.drawable.nutrient_carb;
                        } else if (nutrientIntakeDto.getNutrientName().equals("Proteins")){
                            nutrientIconId = R.drawable.nutrient_protein;
                        } else if (nutrientIntakeDto.getNutrientName().equals("Fats")){
                            nutrientIconId = R.drawable.nutrient_fat;
                        } else if (nutrientIntakeDto.getNutrientName().equals("Fibre")){
                            nutrientIconId = R.drawable.nutrient_fibre;
                        } else if (nutrientIntakeDto.getNutrientName().equals("Sugars")){
                            nutrientIconId = R.drawable.nutrient_sugar;
                        } else if (nutrientIntakeDto.getNutrientName().equals("Sodium")){
                            nutrientIconId = R.drawable.nutrient_sodium;
                        }
                        NutrientIntake nutrientIntake = new NutrientIntake(
                                nutrientIntakeDto.getId(),
                                nutrientIntakeDto.getNutrientName(),
                                nutrientIconId,
                                nutrientIntakeDto.getCurrentNutrient(),
                                nutrientIntakeDto.getTotalNutrient(),
                                new MassUnit(nutrientIntakeDto.getIntakeUnit()));
                        res.add(nutrientIntake);
                    }
                }
                callback.onDataLoaded(res);
            }

            @Override
            public void onFailure(Call<List<NutrientIntakeDto>> call, Throwable t) {
            }
        });
        return res;
    }

    @Override
    public void initializeNutrientIntake(long userId, OnIntakeAddedCallback callback) {  // Add callback parameter
        List<NutrientIntakeDto> intakeDtosToBeInserted = new ArrayList<>();
        intakeDtosToBeInserted.add(new NutrientIntakeDto("g", 216, 0, "Carbs", userId));
        intakeDtosToBeInserted.add(new NutrientIntakeDto("g", 216, 0, "Proteins", userId));
        intakeDtosToBeInserted.add(new NutrientIntakeDto("g", 82, 0, "Fats", userId));
        intakeDtosToBeInserted.add(new NutrientIntakeDto("g", 30, 0, "Fibre", userId));
        intakeDtosToBeInserted.add(new NutrientIntakeDto("g", 25, 0, "Sugars", userId));
        intakeDtosToBeInserted.add(new NutrientIntakeDto("g", 1.5, 0, "Sodium", userId));

        // Track successful inserts
        final int[] successfulInserts = {0};
        final int totalInserts = intakeDtosToBeInserted.size();

        for(int i = 0; i < intakeDtosToBeInserted.size(); i++){
            Call<NutrientIntakeDto> call = nutrientIntakeService.addNutrientIntake(userId, intakeDtosToBeInserted.get(i));
            call.enqueue(new Callback<NutrientIntakeDto>() {
                @Override
                public void onResponse(Call<NutrientIntakeDto> call, Response<NutrientIntakeDto> response) {
                    if (response.isSuccessful()) {
                        successfulInserts[0]++;
                        if(successfulInserts[0] == totalInserts) {
                            callback.onIntakeAdded(true); // All inserts successful
                        }
                    } else {
                        callback.onIntakeAdded(false);
                    }
                }

                @Override
                public void onFailure(Call<NutrientIntakeDto> call, Throwable t) {
                    callback.onIntakeAdded(false);
                }
            });
        }
    }

    @Override
    public void updateNutrientIntake(long userId, List<Double> nutrientInfo, DatabaseNutrientIntakeProvider.OnIntakeUpdateCallback callback){
        List<NutrientIntakeDto> intakeDtosToBeUpdated = new ArrayList<>();
        intakeDtosToBeUpdated.add(new NutrientIntakeDto("g", 216, nutrientInfo.get(0), "Carbs", userId));
        intakeDtosToBeUpdated.add(new NutrientIntakeDto("g", 216, nutrientInfo.get(1), "Proteins", userId));
        intakeDtosToBeUpdated.add(new NutrientIntakeDto("g", 82, nutrientInfo.get(2), "Fats", userId));
        intakeDtosToBeUpdated.add(new NutrientIntakeDto("g", 30, nutrientInfo.get(3), "Fibre", userId));
        intakeDtosToBeUpdated.add(new NutrientIntakeDto("g", 25, nutrientInfo.get(4), "Sugars", userId));
        intakeDtosToBeUpdated.add(new NutrientIntakeDto("g", 1.5, nutrientInfo.get(5), "Sodium", userId));

        Call<List<NutrientIntakeDto>> call = nutrientIntakeService.updateUserNutrientIntakes(userId, intakeDtosToBeUpdated);

        call.enqueue(new Callback<List<NutrientIntakeDto>>() {
            @Override
            public void onResponse(Call<List<NutrientIntakeDto>> call, Response<List<NutrientIntakeDto>> response) {
                callback.onIntakeUpdate(true);
                Log.d("debug", "we updated guys!!!");
            }
            @Override
            public void onFailure(Call<List<NutrientIntakeDto>> call, Throwable t) {
                callback.onIntakeUpdate(false);
                Log.d("debug", "we died already!");
            }
        });

    }

    public interface OnDataLoadedCallback {
        void onDataLoaded(List<NutrientIntake> nutrientIntakes);
    }

    public interface OnIntakeAddedCallback {
        void onIntakeAdded(boolean success);
    }

    public interface OnIntakeUpdateCallback{
        void onIntakeUpdate(boolean success);
    }
}
