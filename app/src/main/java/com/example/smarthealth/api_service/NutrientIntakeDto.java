package com.example.smarthealth.api_service;

import java.util.Calendar;

public class NutrientIntakeDto {
    private Long intakeId;
    private String nutrientName;
    private double currentNutrient;
    private double totalNutrient;
    private String intakeUnit;
    private Long userId;

    public NutrientIntakeDto(String intakeUnit, double totalNutrient, double currentNutrient, String nutrientName, Long userId) {
        this.intakeUnit = intakeUnit;
        this.totalNutrient = totalNutrient;
        this.currentNutrient = currentNutrient;
        this.nutrientName = nutrientName;
        this.userId = userId;
    }

    public Long getId() {
        return intakeId;
    }

    public void setId(Long intakeId) {
        this.intakeId = intakeId;
    }

    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public double getCurrentNutrient() {
        return currentNutrient;
    }

    public void setCurrentNutrient(double currentNutrient) {
        this.currentNutrient = currentNutrient;
    }

    public double getTotalNutrient() {
        return totalNutrient;
    }

    public void setTotalNutrient(double totalNutrient) {
        this.totalNutrient = totalNutrient;
    }

    public String getIntakeUnit() {
        return intakeUnit;
    }

    public void setIntakeUnit(String intakeUnit) {
        this.intakeUnit = intakeUnit;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}