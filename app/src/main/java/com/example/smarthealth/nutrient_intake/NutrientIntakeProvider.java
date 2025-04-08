package com.example.smarthealth.nutrient_intake;

import android.widget.ArrayAdapter;

import com.example.smarthealth.api_service.NutrientIntakeDto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface NutrientIntakeProvider {
    // void hasUserIntake(long userId, DatabaseNutrientIntakeProvider.OnIntakeCheckCallback callback);
    List<NutrientIntake> getNutrientIntakes(long userId, DatabaseNutrientIntakeProvider.OnDataLoadedCallback onDataLoadedCallback);
    void initializeNutrientIntake(long userId, DatabaseNutrientIntakeProvider.OnIntakeAddedCallback callback);
    void updateNutrientIntake(ArrayList<Long> intakeId, ArrayList<Double> nutrientInfo, DatabaseNutrientIntakeProvider.OnIntakeUpdateCallback callback);
}
