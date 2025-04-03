package com.example.smarthealth.Inventory;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class MedicineButton {
    public String medicineName;
    public String medicineCategory;
    public int medicineAmount;
    public Drawable medicineImage;
    public String medicineDosage;
    public String medicineContains;
    public String medicineSideEffect;
    public ArrayList<String> medicineType;

    MedicineButton(String medicineName, String medicineCategory, int medicineAmount, Drawable medicineImage,
                   String medicineDosage, String medicineContains, String medicineSideEffect, ArrayList<String> medicineType) {
        this.medicineName = medicineName;
        this.medicineCategory = medicineCategory;
        this.medicineAmount = medicineAmount;
        this.medicineImage = medicineImage;
        this.medicineDosage = medicineDosage;
        this.medicineContains = medicineContains;
        this.medicineSideEffect = medicineSideEffect;
        this.medicineType = medicineType;
    }
    public String getMedicineName() {
        return medicineName;
    }

    public String getMedicineCategory(){
        return medicineCategory;
    }

    public String getMedicineDosage() {
        return medicineDosage;
    }
    public String getMedicineContains() {
        return medicineContains;
    }
    public String getMedicineSideEffect() {
        return medicineSideEffect;
    }


    public int getMedicineAmount() {
        return medicineAmount;
    }

    public Drawable getMedicineImage() {
        return medicineImage;
    }

     public ArrayList<String> getMedicineType(){return this.medicineType;}

}

