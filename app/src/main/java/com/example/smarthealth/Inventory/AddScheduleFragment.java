package com.example.smarthealth.Inventory;

import android.app.DatePickerDialog;
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
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class AddScheduleFragment extends DialogFragment{
    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    private MaterialButton startDate;
    private MaterialButton endDate;

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

        startDate = popupView.findViewById(R.id.dateSelect1);
        endDate = popupView.findViewById(R.id.dateSelect2);
        initDatePicker();

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        // Add to schedule
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

    private void initDatePicker(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Start Date Picker
        startDatePickerDialog = new DatePickerDialog(requireContext(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            startDateCalendar.set(selectedDay, selectedMonth, selectedYear);
            if(startDateCalendar.after(endDateCalendar)){
                Toast.makeText(requireContext(), "Start Date must be before End Date", Toast.LENGTH_SHORT).show();
            }
            else{
                selectedMonth += 1;
                String date = makeDateString(selectedDay, selectedMonth, selectedYear);
                startDate.setText(date);
            }
        }, year, month, day);

        // End Date Picker
        endDatePickerDialog = new DatePickerDialog(requireContext(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            endDateCalendar.set(selectedDay,selectedMonth, selectedYear);
            if(endDateCalendar.before(startDateCalendar)){
                Toast.makeText(requireContext(),"End Date should be after Start Date", Toast.LENGTH_SHORT).show();
            }
            else{
                selectedMonth += 1;
                String date = makeDateString(selectedDay, selectedMonth, selectedYear);
                endDate.setText(date);
            }
        }, year, month, day);
    }

    private String makeDateString(int day, int month, int year){
        return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month){
        switch (month){
            case 1: return "JAN";
            case 2: return "FEB";
            case 3: return "MAR";
            case 4: return "APR";
            case 5: return "MAY";
            case 6: return "JUN";
            case 7: return "JUL";
            case 8: return "AUG";
            case 9: return "SEP";
            case 10: return "OCT";
            case 11: return "NOV";
            case 12: return "DEC";
            default: return null;
        }
    }


}
