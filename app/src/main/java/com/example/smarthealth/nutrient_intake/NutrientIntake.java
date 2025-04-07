package com.example.smarthealth.nutrient_intake;

import com.example.smarthealth.nutrient_intake.units.Unit;

public class NutrientIntake {
    private String nutrientName;
    private int nutrientIconId;
    private double currentNutrient;
    private double totalNutrient;
    private Unit intakeUnit;

    public NutrientIntake(String nutrientName, int nutrientIconId, double currentNutrient, double totalNutrient, Unit intakeUnit) {
        this.nutrientName = nutrientName;
        this.nutrientIconId = nutrientIconId;
        this.currentNutrient = currentNutrient;
        this.totalNutrient = totalNutrient;
        this.intakeUnit = intakeUnit;
    }

    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public int getNutrientIconId() {
        return nutrientIconId;
    }

    public void setNutrientIconId(int nutrientIconId) {
        this.nutrientIconId = nutrientIconId;
    }

    public double getCurrentNutrient() {
        return currentNutrient;
    }

    public void setCurrentNutrient(int currentNutrient) {
        this.currentNutrient = currentNutrient;
    }

    public double getTotalNutrient() {
        return totalNutrient;
    }

    public void setTotalNutrient(int totalNutrient) {
        this.totalNutrient = totalNutrient;
    }

    public Unit getIntakeUnit() {
        return intakeUnit;
    }

    public void setIntakeUnit(Unit intakeUnit) {
        this.intakeUnit = intakeUnit;
    }
}
