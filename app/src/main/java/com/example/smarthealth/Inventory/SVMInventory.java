package com.example.smarthealth.Inventory;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SVMInventory extends ViewModel {
    private ArrayList<MedicineButton> pillsButtonList = new ArrayList<>();
    private ArrayList<MedicineButton> liquidsButtonList = new ArrayList<>();
    private ArrayList<MedicineButton> othersButtonList = new ArrayList<>();
    public ArrayList<MedicineButton> getPillsButtonList(){
        return pillsButtonList;
    }
    public void setPillsButtonList(ArrayList<MedicineButton> list){
        this.pillsButtonList = list;
    }

    public ArrayList<MedicineButton> getLiquidsButtonList(){
        return liquidsButtonList;
    }
    public void setLiquidsButtonList(ArrayList<MedicineButton> list){
        this.liquidsButtonList = list;
    }

    public ArrayList<MedicineButton> getOthersButtonList(){
        return othersButtonList;
    }
    public void setOthersButtonList(ArrayList<MedicineButton> list){
        this.othersButtonList = list;
    }

}

