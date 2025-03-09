package com.example.smarthealth;

import android.graphics.Color;
import android.icu.lang.UCharacter;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import android.graphics.Typeface;
import java.util.ArrayList;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.content.ContextCompat;

public class MedicineContainer {
    public LinearLayout classPointer;
    public LinearLayout containerLayout;
    private int buttonInContainer = 0;
    private ArrayList<CardView> cardList;

    MedicineContainer(Context context){
       containerLayout = new LinearLayout(context);
       containerLayout.setOrientation(LinearLayout.HORIZONTAL);
       containerLayout.setPadding(0, 20, 0, 20);
       containerLayout.setBackgroundColor(Color.TRANSPARENT);
        // Layout parameters to ensure full width and spacing between rows
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.topMargin = 10; // Space between rows
        containerLayout.setWeightSum(2);
        containerLayout.setLayoutParams(layoutParams);

        cardList = new ArrayList<CardView>();
    }


    public void addMediButton(Context context){


        // Increment count in the current row
        buttonInContainer++;
    }

    public LinearLayout getContainerLayout(){
        return containerLayout;
    }

    public int getButtonCount(){
        return buttonInContainer;
    }
}

