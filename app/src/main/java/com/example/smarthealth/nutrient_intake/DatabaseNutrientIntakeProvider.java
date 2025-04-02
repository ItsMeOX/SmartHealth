package com.example.smarthealth.nutrient_intake;

import com.example.smarthealth.R;
import com.example.smarthealth.nutrient_intake.units.MassUnit;
import com.example.smarthealth.nutrient_intake.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class DatabaseNutrientIntakeProvider implements NutrientIntakeProvider {
    @Override
    public List<NutrientIntake> getNutrientIntakes() {
        // TODO: to be connected to database by Tristan + AI by Haile.

        List<NutrientIntake> res = new ArrayList<>();
        res.add(new NutrientIntake("Carbs", R.drawable.nutrient_carb, 50.2, 216.0, MassUnit.GRAM));
        res.add(new NutrientIntake("Proteins", R.drawable.nutrient_protein, 46.3, 216.0, MassUnit.GRAM));
        res.add(new NutrientIntake("Fats", R.drawable.nutrient_fat, 92.2, 82.0, MassUnit.GRAM));
        res.add(new NutrientIntake("Fibre", R.drawable.nutrient_fibre, 15.0, 30.0, MassUnit.GRAM));
        res.add(new NutrientIntake("Sugars", R.drawable.nutrient_sugar, 12.5, 25.0, MassUnit.GRAM));
        res.add(new NutrientIntake("Sodium", R.drawable.nutrient_sodium, 0.9, 1.5, MassUnit.GRAM));
        return res;
    }
}
