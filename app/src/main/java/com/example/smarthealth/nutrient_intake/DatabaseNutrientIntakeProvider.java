package com.example.smarthealth.nutrient_intake;

import static android.app.PendingIntent.getActivity;

import android.util.Log;

import com.example.smarthealth.R;
import com.example.smarthealth.api_service.NutrientIntakeDto;
import com.example.smarthealth.api_service.NutrientIntakeService;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.nutrient_intake.units.MassUnit;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseNutrientIntakeProvider implements NutrientIntakeProvider {

    private NutrientIntakeService nutrientIntakeService = RetrofitClient.getInstance().create(NutrientIntakeService.class);
    @Override
    public List<NutrientIntake> getNutrientIntakes(long userId, OnDataLoadedCallback callback) {
        // TODO: to be connected to database by Tristan + AI by Haile.
        List<NutrientIntake> res = new ArrayList<>();

        Call<List<NutrientIntakeDto>> call = nutrientIntakeService.getUserDailyEvents(16);

        call.enqueue(new Callback<List<NutrientIntakeDto>>() {
            @Override
            public void onResponse(Call<List<NutrientIntakeDto>> call, Response<List<NutrientIntakeDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for (NutrientIntakeDto nutrientIntakeDto : response.body()) {
                        NutrientIntake nutrientIntake = new NutrientIntake(nutrientIntakeDto.getNutrientName(),
                                R.drawable.nutrient_carb,
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

    public interface OnDataLoadedCallback {
        void onDataLoaded(List<NutrientIntake> nutrientIntakes);
    }

}
