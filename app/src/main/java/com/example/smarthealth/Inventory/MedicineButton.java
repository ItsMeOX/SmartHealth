package com.example.smarthealth.Inventory;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class MedicineButton {
    public String medicineName;
    public String medicineDesc;

    public int medicineAmount;

    public Drawable medicineImage;

    public String medicineInfo;

    public String medicineType;

    MedicineButton(String medicineName, String medicineDesc, int medicineAmount, Drawable medicineImage, String medicineInfo, String medicineType) {
        this.medicineName = medicineName;
        this.medicineDesc = medicineDesc;
        this.medicineAmount = medicineAmount;
        this.medicineImage = medicineImage;
        this.medicineInfo = medicineInfo;
        this.medicineType = medicineType;
    }
    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineDesc() {
        return medicineDesc;
    }

    public void setMedicineDesc(String medicineDesc) {
        this.medicineDesc = medicineDesc;
    }

    public int getMedicineAmount() {
        return medicineAmount;
    }

    public void setMedicineAmount(int medicineAmount) {
        this.medicineAmount = medicineAmount;
    }

    public Drawable getMedicineImage() {
        return medicineImage;
    }

    public void setMedicineImage(Drawable medicineImage) {
        this.medicineImage = medicineImage;
    }

    public String getMedicineInfo(){return this.medicineInfo;}

    public void setMedicineInfo(String info){this.medicineInfo = info; }

    public  void setMedicineType(String type){this.medicineType = type;}
    public String getMedicineType(){return this.medicineType;}

}

