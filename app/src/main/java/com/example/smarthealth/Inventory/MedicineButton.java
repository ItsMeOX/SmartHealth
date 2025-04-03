package com.example.smarthealth.Inventory;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class MedicineButton {
    public String medicineName;
    public String medicineCategory;
    public String medicineDesc;
    public int medicineAmount;
    public Drawable medicineImage;
    public String medicineInfo;
    public ArrayList<String> medicineType;

    MedicineButton(String medicineName, String medicineCategory, String medicineDesc, int medicineAmount, Drawable medicineImage, String medicineInfo, ArrayList<String> medicineType) {
        this.medicineName = medicineName;
        this.medicineCategory = medicineCategory;
        this.medicineDesc = medicineDesc;
        this.medicineAmount = medicineAmount;
        this.medicineImage = medicineImage;
        this.medicineInfo = medicineInfo;
        this.medicineType = medicineType;
    }
    public String getMedicineName() {
        return medicineName;
    }

    public String getMedicineCategory(){
        return medicineCategory;
    }

    public String getMedicineDesc() {
        return medicineDesc;
    }


    public int getMedicineAmount() {
        return medicineAmount;
    }

    public Drawable getMedicineImage() {
        return medicineImage;
    }

    public String getMedicineInfo(){return this.medicineInfo;}

     public ArrayList<String> getMedicineType(){return this.medicineType;}

}

