package com.example.smarthealth.Inventory;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class MedicineButton {
    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    private Long medicineId;
    public String medicineName;
    public String medicineCategory;
    public int medicineAmount;
    public String medicineImage;
    public String medicineDosage;
    public String medicineContains;
    public String medicineSideEffect;
    public ArrayList<String> medicineType;

    MedicineButton(Long medicineId, String medicineName, String medicineCategory, int medicineAmount, String medicineImage,
                   String medicineDosage, String medicineContains, String medicineSideEffect, ArrayList<String> medicineType) {
        this.medicineId = medicineId;
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

    public String getMedicineImage() {
        return medicineImage;
    }

     public ArrayList<String> getMedicineType(){return this.medicineType;}

}

