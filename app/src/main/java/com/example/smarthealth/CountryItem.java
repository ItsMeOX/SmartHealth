package com.example.smarthealth;

public class CountryItem {
    private int flagImage;
    private String countryCode;

    public CountryItem(int flagImage, String countryCode) {
        this.flagImage = flagImage;
        this.countryCode = countryCode;
    }

    public int getFlagImage() {
        return flagImage;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
