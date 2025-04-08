package com.example.smarthealth.api_service;

import com.google.gson.annotations.JsonAdapter;

public class MedicineDto {
    private Long id;
    private String medicineName;
    private String medicineCategory;
    private int medicineAmount;
    @JsonAdapter(ByteArrayDeserializer.class)
    private byte[] medicineImage;
    private String medicineDosage;
    private String medicineContains;
    private String medicineSideEffect;
    private String medicineType;
    private Long userId;

    public MedicineDto(String medicineName, int medicineAmount, String medicineCategory,
                       byte[] medicineImage, String medicineDosage, String medicineContains,
                       String medicineType, String medicineSideEffect) {
        this.medicineName = medicineName;
        this.medicineAmount = medicineAmount;
        this.medicineCategory = medicineCategory;
        this.medicineImage = medicineImage;
        this.medicineDosage = medicineDosage;
        this.medicineContains = medicineContains;
        this.medicineType = medicineType;
        this.medicineSideEffect = medicineSideEffect;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public String getMedicineSideEffect() {
        return medicineSideEffect;
    }

    public void setMedicineSideEffect(String medicineSideEffect) {
        this.medicineSideEffect = medicineSideEffect;
    }

    public String getMedicineContains() {
        return medicineContains;
    }

    public void setMedicineContains(String medicineContains) {
        this.medicineContains = medicineContains;
    }

    public String getMedicineDosage() {
        return medicineDosage;
    }

    public void setMedicineDosage(String medicineDosage) {
        this.medicineDosage = medicineDosage;
    }

    public byte[] getMedicineImage() {
        return medicineImage;
    }

    public void setMedicineImage(byte[] medicineImage) {
        this.medicineImage = medicineImage;
    }

    public int getMedicineAmount() {
        return medicineAmount;
    }

    public void setMedicineAmount(int medicineAmount) {
        this.medicineAmount = medicineAmount;
    }

    public String getMedicineCategory() {
        return medicineCategory;
    }

    public void setMedicineCategory(String medicineCategory) {
        this.medicineCategory = medicineCategory;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
