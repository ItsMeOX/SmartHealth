package com.example.smarthealth.nutrient_intake.units;

import androidx.annotation.NonNull;

public abstract class Unit {
    private final String symbol;

    public Unit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @NonNull
    @Override
    public String toString() {
        return symbol;
    }
}
