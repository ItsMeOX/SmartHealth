package com.example.smarthealth.Inventory;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;

import com.example.smarthealth.R;
import java.util.ArrayList;

public class MedicineInfoPage {
    public static void showInfo(Context context, MedicineButton model, View parentView){
        View popupView = LayoutInflater.from(context).inflate(R.layout.medicine_fullview, null);

        // Find respective views to be filled up
        ImageView mediImage = popupView.findViewById(R.id.infoImage);
        TextView mediName = popupView.findViewById(R.id.infoName);
        TextView mediAmount = popupView.findViewById(R.id.infoAmount);
        TextView mediInfo = popupView.findViewById(R.id.mediFullInfo);
        LinearLayout mediTags = popupView.findViewById(R.id.tags);

        // Set model's information to be displayed in info page
        mediInfo.setText(model.getMedicineInfo());
        mediImage.setImageDrawable(model.getMedicineImage());
        mediName.setText(model.getMedicineName().trim());
        if(model.getMedicineCategory().equals("Liquids")){
            String s = String.format(Integer.toString(model.getMedicineAmount()) + " ml");
            mediAmount.setText(s);
        }
        else if(model.medicineCategory.equals("Pills")){
            String s = String.format(Integer.toString(model.getMedicineAmount()) + " Tab");
            mediAmount.setText(s);
        }
        ArrayList<String> tags = model.getMedicineType();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.rightMargin = 18;
        for(int i = 0; i<tags.size();i++){
            TextView textView = new TextView(context);
            textView.setText(tags.get(i));
            textView.setTextSize(18);
            textView.setBackgroundResource(R.drawable.rounded);
            textView.setPadding(20, 5, 20, 5);
            ColorStateList colorStateList = ColorStateList.valueOf(getMedicineTagColor(tags.get(i),context));
            textView.setBackgroundTintList(colorStateList);
            textView.setLayoutParams(layoutParams);
            mediTags.addView(textView);
        }

        // Display popup window
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true);
        popupWindow.showAtLocation(parentView.getRootView(), Gravity.CENTER,0,0);

        ImageView goBack = popupView.findViewById(R.id.backarrow);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        AppCompatButton addToSchedule = popupView.findViewById(R.id.addSchedule);
        addToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                AddScheduleFragment scheduleFragment = new AddScheduleFragment();
                scheduleFragment.show(fragmentManager, "Add To Schedule");
            }
        });

        AppCompatButton removeFromSchedule = popupView.findViewById(R.id.removeSchedule);
        removeFromSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove from database using model data.
            }
        });
    }
    public static int getMedicineTagColor(String name, Context context) {
        switch (name.toLowerCase()) { // Convert to lowercase for case insensitivity
            case "cough":
                return context.getResources().getColor(R.color.Cough, context.getTheme());
            case "fever":
                return context.getResources().getColor(R.color.Fever, context.getTheme());
            case "cold":
                return context.getResources().getColor(R.color.Cold, context.getTheme());
            case "diarrhoea":
                return context.getResources().getColor(R.color.Diarrhoea, context.getTheme());
            case "phlegm":
                return context.getResources().getColor(R.color.Phlegm, context.getTheme());
            case "painkiller":
                return context.getResources().getColor(R.color.Painkiller, context.getTheme());
            case "diabetes":
                return context.getResources().getColor(R.color.Diabetes, context.getTheme());
            case "high cholesterol":
                return context.getResources().getColor(R.color.HighCholesterol, context.getTheme());
            case "dry eyes":
                return context.getResources().getColor(R.color.DryEyes, context.getTheme());
            case "high-blood pressure":
                return context.getResources().getColor(R.color.High_BloodPressure, context.getTheme());
            default:
                return context.getResources().getColor(R.color.Others, context.getTheme()); // Default color
        }
    }

}
