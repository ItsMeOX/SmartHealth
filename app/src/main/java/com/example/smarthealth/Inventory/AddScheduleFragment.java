package com.example.smarthealth.Inventory;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import com.example.smarthealth.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddScheduleFragment extends DialogFragment{


    @Override
    public void onStart(){
        super.onStart();
        if (getDialog() != null)
        {int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Window window = getDialog().getWindow();
            window.setLayout(width, height);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View popupView = inflater.inflate(R.layout.add_to_schedule, null);

        AppCompatButton confirmButton = popupView.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText intake = popupView.findViewById(R.id.intake);
                EditText time1 = popupView.findViewById(R.id.time1);
                EditText time2 = popupView.findViewById(R.id.time2);
                EditText time3 = popupView.findViewById(R.id.time3);

                int intakeValue = Integer.parseInt(intake.getText().toString().trim());
                String time1Value = time1.getText().toString().trim();
                String time2Value = time2.getText().toString().trim();
                String time3Value = time3.getText().toString().trim();

                if(intakeValue > 4 || intakeValue < 0){
                    Toast.makeText(requireContext(),"Please Key in Value between 1 to 3",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(intakeValue == 1){
                    if(time1Value.isEmpty()){
                        Toast.makeText(requireContext(),"Please Key In Timeslot1", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!time2Value.isEmpty() || !time3Value.isEmpty()){
                        Toast.makeText(requireContext(),"Key in 1 timeslot only", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!checkValidTime(time1Value)){
                        Toast.makeText(requireContext(),"Wrong format for time",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(intakeValue == 2){
                    if(time1Value.isEmpty() || time2Value.isEmpty()){
                        Toast.makeText(requireContext(),"Please Key In 2 Timeslot", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!time3Value.isEmpty()){
                        Toast.makeText(requireContext(), "Key in Timeslot 1 and 2 only", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!checkValidTime(time1Value) || !checkValidTime(time2Value)){
                        Toast.makeText(requireContext(),"Wrong format for time",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                if(intakeValue == 3){
                    if(time1Value.isEmpty() || time2Value.isEmpty() || time3Value.isEmpty()){
                        Toast.makeText(requireContext(),"Please Key In 3 Timeslots", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!checkValidTime(time1Value) || !checkValidTime(time2Value) || !checkValidTime(time3Value)){
                        Toast.makeText(requireContext(),"Wrong format for time",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(checkValidTime(time1Value) && checkValidTime(time2Value) && checkValidTime(time3Value)){
                    Bundle result = new Bundle();
                    // Add to database //
                    result.putString("Intake", intake.toString().trim());
                    result.putString("Time 1", time1Value);
                    result.putString("Time 2", time2Value);
                    result.putString("Time 3", time3Value);

                    getParentFragmentManager().setFragmentResult("Schedule", result);
                    dismiss();
                }
            }
        });



        return popupView;
    }

    public boolean checkValidTime(String time){
        if(time.isEmpty()){return true;}
        String[] time1 = time.split(":");
        int hour = Integer.parseInt(time1[0]);
        int minutes = Integer.parseInt(time1[1]);
        return hour > 0 && hour < 24 && minutes < 60 && minutes >= 0;
    }


}
