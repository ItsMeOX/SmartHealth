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
        TextView mediDosage = popupView.findViewById(R.id.mediDosage);
        TextView mediContain = popupView.findViewById(R.id.mediContain);
        TextView mediSideEffect = popupView.findViewById(R.id.mediSideEffect);
        LinearLayout mediTags = popupView.findViewById(R.id.tags);

        // Set model's information to be displayed in info page
        mediDosage.setText(model.getMedicineDosage());
        mediContain.setText(model.getMedicineContains());
        mediSideEffect.setText(model.getMedicineSideEffect());
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

        // Add medicine tags
        TagManager.addTags(context, tags,mediTags);

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

}
