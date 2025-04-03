package com.example.smarthealth.api_service;

import java.util.Calendar;

public class NutrientIntakeDto {
    private Long id;
    private String nutrientName;
    private double currentNutrient;
    private double totalNutrient;
    private String intakeUnit;
    private Calendar intakeDate;
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Calendar getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Calendar intakeDate) {
        this.intakeDate = intakeDate;
    }
}