package com.example.smarthealth;

public class MedicineButton {
    public String medicineName;
    public String medicineDesc;

    public int medicineAmount;

    public int medicineImage;

    MedicineButton(String medicineName, String medicineDesc, int medicineAmount, int medicineImage) {
        this.medicineName = medicineName;
        this.medicineDesc = medicineDesc;
        this.medicineAmount = medicineAmount;
        this.medicineImage = medicineImage;
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

    public int getMedicineImage() {
        return medicineImage;
    }

    public void setMedicineImage(int medicineImage) {
        this.medicineImage = medicineImage;
    }

}

