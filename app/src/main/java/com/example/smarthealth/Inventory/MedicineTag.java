package com.example.smarthealth.Inventory;

public class MedicineTag {
    private final String tagName;
    private final int backgroundColour;

    public MedicineTag(String s , int colour){
        this.tagName = s;
        this.backgroundColour = colour;
    }

    public String getName(){return this.tagName;}
    public int getBackgroundColour(){return this.backgroundColour;}

}
