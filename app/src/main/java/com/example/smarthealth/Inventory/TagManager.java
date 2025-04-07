package com.example.smarthealth.Inventory;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.smarthealth.R;

import java.util.ArrayList;

public class TagManager {
    public static int getMedicineTagColor(String name, Context context) {
        switch (name.toLowerCase()) { // Convert to lowercase for case insensitivity
            case "antibiotics":
                return  ContextCompat.getColor(context, R.color.AntiBiotic);
            case "cough":
                return ContextCompat.getColor(context, R.color.Cough);
            case "fever":
                return ContextCompat.getColor(context, R.color.Fever);
            case "cold":
                return ContextCompat.getColor(context, R.color.Cold);
            case "diarrhoea":
                return ContextCompat.getColor(context, R.color.Diarrhoea);
            case "phlegm":
                return ContextCompat.getColor(context, R.color.Phlegm);
            case "painkiller":
                return ContextCompat.getColor(context, R.color.Painkiller);
            case "diabetes":
                return ContextCompat.getColor(context, R.color.Diabetes);
            case "high cholesterol":
                return ContextCompat.getColor(context, R.color.HighCholesterol);
            case "dry eyes":
                return ContextCompat.getColor(context, R.color.DryEyes);
            case "high-blood pressure":
                return ContextCompat.getColor(context, R.color.High_BloodPressure);
            default:
                return ContextCompat.getColor(context, R.color.Others);
        }
    }

    public static ArrayList<String> addTags(Context context, ArrayList<String> list, LinearLayout tagView) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.leftMargin = 18;
        for (int i = 0; i < list.size(); i++) {
            TextView textView = new TextView(context);
            textView.setText(list.get(i));
            textView.setTextSize(18);
            textView.setTextColor(ContextCompat.getColor(context, R.color.white));
            textView.setBackgroundResource(R.drawable.rounded);
            textView.setPadding(20, 5, 20, 5);
            ColorStateList colorStateList = ColorStateList.valueOf(TagManager.getMedicineTagColor(list.get(i), context));
            textView.setBackgroundTintList(colorStateList);
            textView.setLayoutParams(layoutParams);
            tagView.addView(textView);
        }
        return list;
    }


}
