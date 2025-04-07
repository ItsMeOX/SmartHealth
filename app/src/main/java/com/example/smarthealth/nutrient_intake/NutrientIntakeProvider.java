package com.example.smarthealth.nutrient_intake;

import com.example.smarthealth.api_service.NutrientIntakeDto;

import java.util.List;

public interface NutrientIntakeProvider {
    // void hasUserIntake(long userId, DatabaseNutrientIntakeProvider.OnIntakeCheckCallback callback);
    List<NutrientIntake> getNutrientIntakes(long userId, DatabaseNutrientIntakeProvider.OnDataLoadedCallback onDataLoadedCallback);
    void initializeNutrientIntake(long userId, DatabaseNutrientIntakeProvider.OnIntakeAddedCallback callback);
}
