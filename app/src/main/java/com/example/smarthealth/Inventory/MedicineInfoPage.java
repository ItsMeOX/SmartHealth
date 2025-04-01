package com.example.smarthealth.Inventory;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.example.smarthealth.R;

import org.w3c.dom.Text;

public class MedicineInfoPage {
    public static void showInfo(Context context, MedicineButton model, View parentView){
        View popupView = LayoutInflater.from(context).inflate(R.layout.medicine_fullview, null);

        // Find respective views to be filled up
        ImageView mediImage = popupView.findViewById(R.id.infoImage);
        TextView mediName = popupView.findViewById(R.id.infoName);
        TextView mediAmount = popupView.findViewById(R.id.infoAmount);
        TextView mediInfo = popupView.findViewById(R.id.mediFullInfo);

        mediInfo.setText(model.getMedicineInfo());
        // Set model's information to be displayed in info page
        mediImage.setImageDrawable(model.getMedicineImage());
        mediName.setText(model.getMedicineName().trim());
        if(model.getMedicineType().equals("Liquids")){
            String s = String.format(Integer.toString(model.getMedicineAmount()) + " ml");
            mediAmount.setText(s);
        }
        else if(model.getMedicineType().equals("Pills")){
            String s = String.format(Integer.toString(model.getMedicineAmount()) + " Tab");
            mediAmount.setText(s);
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
    }
}
