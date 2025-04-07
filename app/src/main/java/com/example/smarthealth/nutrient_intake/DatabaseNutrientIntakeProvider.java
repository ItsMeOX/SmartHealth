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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseNutrientIntakeProvider implements NutrientIntakeProvider {
    private NutrientIntakeService nutrientIntakeService ;
    private Context context;

    public DatabaseNutrientIntakeProvider(Context context) {
        this.context = context;
        nutrientIntakeService = RetrofitClient.getInstance().create(NutrientIntakeService.class);
    }

//    public void hasUserIntake(long userId, OnIntakeCheckCallback callback) {
//        Call<Boolean> call = nutrientIntakeService.checkIfUserHasIntakes(userId);
//        call.enqueue(new Callback<Boolean>() {
//            @Override
//            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
//                callback.onIntakeChecked(response.isSuccessful() && response.body() != null && response.body());
//            }
//
//            @Override
//            public void onFailure(Call<Boolean> call, Throwable t) {
//                callback.onIntakeChecked(false);
//            }
//        });
//    }
    @Override
    public List<NutrientIntake> getNutrientIntakes(long userId, OnDataLoadedCallback callback) {
        // TODO: to be connected to database by Tristan + AI by Haile.
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
                        NutrientIntake nutrientIntake = new NutrientIntake(nutrientIntakeDto.getNutrientName(),
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

//        res.add(new NutrientIntake("Carbs", R.drawable.nutrient_carb, 50.2, 216.0, MassUnit.GRAM));
//        res.add(new NutrientIntake("Proteins", R.drawable.nutrient_protein, 46.3, 216.0, MassUnit.GRAM));
//        res.add(new NutrientIntake("Fats", R.drawable.nutrient_fat, 92.2, 82.0, MassUnit.GRAM));
//        res.add(new NutrientIntake("Fibre", R.drawable.nutrient_fibre, 15.0, 30.0, MassUnit.GRAM));
//        res.add(new NutrientIntake("Sugars", R.drawable.nutrient_sugar, 12.5, 25.0, MassUnit.GRAM));
//        res.add(new NutrientIntake("Sodium", R.drawable.nutrient_sodium, 0.9, 1.5, MassUnit.GRAM));
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

    public interface OnDataLoadedCallback {
        void onDataLoaded(List<NutrientIntake> nutrientIntakes);
    }

//    public interface OnIntakeCheckCallback {
//        void onIntakeChecked(boolean hasIntake);
//    }

    public interface OnIntakeAddedCallback {
        void onIntakeAdded(boolean success);
    }
}
