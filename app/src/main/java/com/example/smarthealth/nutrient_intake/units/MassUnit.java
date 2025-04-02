package com.example.smarthealth.nutrient_intake.units;

public class MassUnit extends Unit {
    public static final MassUnit GRAM = new MassUnit("g");
    public static final MassUnit KILOGRAM = new MassUnit("kg");
    public static final MassUnit MILLIGRAM = new MassUnit("mg");

    private MassUnit(String symbol) {
        super(symbol);
    }
}
