package com.example.smarthealth.nutrient_intake;

import java.util.List;

public interface NutrientIntakeProvider {
    List<NutrientIntake> getNutrientIntakes(long userId, OnDataLoadedCallback onDataLoadedCallback);
}
