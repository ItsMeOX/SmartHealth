package com.example.smarthealth.Inventory;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.example.smarthealth.R;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.api_service.UpcomingScheduleDto;
import com.example.smarthealth.api_service.UpcomingScheduleService;
import com.example.smarthealth.calendar.CalendarUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddScheduleFragment extends DialogFragment{

    private TextInputEditText dateEditText;
    private TextInputEditText timeEditText;
    private TextInputLayout timeInputLayout;
    private TextInputLayout dateInputLayout;
    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    private MaterialButton startDate;
    private MaterialButton endDate;
    private final String dateFormat = "yyyy/MM/dd";
    private SharedPreferences sharedPreferences;
    private long userId;
    private UpcomingScheduleService upcomingScheduleService;
    private MedicineButton model;

    public AddScheduleFragment(MedicineButton model) {
        this.model = model;
    }

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
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);
        upcomingScheduleService = RetrofitClient.getInstance().create(UpcomingScheduleService.class);

        dateEditText = popupView.findViewById(R.id.eventDateMedicineEditText);
        dateInputLayout = popupView.findViewById(R.id.eventDateMedicineTextField);
        timeEditText = popupView.findViewById(R.id.eventTimeMedicineEditText);
        timeInputLayout = popupView.findViewById(R.id.eventTimeMedicineTextField);

        dateEditText.setOnClickListener(v -> openDatePicker(dateEditText));
        timeEditText.setOnClickListener(v -> openTimePicker(timeEditText));

        // Add to schedule
        AppCompatButton confirmButton = popupView.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText intake = popupView.findViewById(R.id.intake);
                String eventDateRange = dateEditText.getText().toString();
                String eventTimeRange = timeEditText.getText().toString();

                int intakeValue = Integer.parseInt(intake.getText().toString().trim());

                if(intakeValue > 4 || intakeValue < 0){
                    Toast.makeText(requireContext(),"Please Key in Value between 1 to 3",Toast.LENGTH_SHORT).show();
                    return;
                }


                String[] dates = eventDateRange.split("-"); // yyyy/MM/dd - yyyy/MM/dd
                String[] times = eventTimeRange.split("-"); // hh:mm - hh:mm

                try {
                    Calendar startDate = CalendarUtil.stringToCalendar(dates[0].trim(), dateFormat);
                    Calendar endDate = CalendarUtil.stringToCalendar(dates[1].trim(), dateFormat);

                    String[] startTimeParts = times[0].trim().split(":");
                    String[] endTimeParts = times[1].trim().split(":");
                    int startHour = Integer.parseInt(startTimeParts[0]);
                    int startMinute = Integer.parseInt(startTimeParts[1]);
                    int endHour = Integer.parseInt(endTimeParts[0]);
                    int endMinute = Integer.parseInt(endTimeParts[1]);

                    startDate.set(Calendar.HOUR_OF_DAY, startHour+8);
                    startDate.set(Calendar.MINUTE, startMinute);
                    startDate.set(Calendar.SECOND, 0);

                    endDate.set(Calendar.HOUR_OF_DAY, endHour+8);
                    endDate.set(Calendar.MINUTE, endMinute);
                    endDate.set(Calendar.SECOND, 0);

                    if (endDate.before(startDate)) {
                        dateInputLayout.setError("Start date or time cannot be after the ending date");
                        timeInputLayout.setError("Start date or time cannot be after the ending date");
                        return;
                    }

                    Call<UpcomingScheduleDto> call = upcomingScheduleService.createSchedule(userId, new UpcomingScheduleDto(
                            model.getMedicineName(),
                            model.getMedicineSideEffect() + "\n" + model.getMedicineDosage(),
                            startDate,
                            "medicine",
                            false,
                            model.getMedicineId(),
                            intakeValue
                    ));
                    call.enqueue(new Callback<UpcomingScheduleDto>() {
                        @Override
                        public void onResponse(Call<UpcomingScheduleDto> call, Response<UpcomingScheduleDto> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Log.d("debug", "we done");
                            }
                        }

                        @Override
                        public void onFailure(Call<UpcomingScheduleDto> call, Throwable t) {
                            if (isAdded() && getActivity() != null) {
                                Log.d("debug", "we f up: " + t.getMessage());
                            }
                        }
                    });

                    dismiss();


                } catch (ParseException | NumberFormatException e) {
                    dateInputLayout.setError("Wrong date or time format");
                    return;
                }
            }
        });
        return popupView;
    }

    private void openDatePicker(TextInputEditText dateEditText) {
        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select a date range")
                .setSelection(new Pair<>(
                        MaterialDatePicker.todayInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                ))
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                Long startDate = selection.first;
                Long endDate = selection.second;

                SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
                String startDateStr = dateFormatter.format(startDate);
                String endDateStr = dateFormatter.format(endDate);
                dateEditText.setText(startDateStr + " - " + endDateStr);
            }
        });
    }

    private void openTimePicker(TextInputEditText timeEditText) {
        MaterialTimePicker startTimePicker = new MaterialTimePicker.Builder()
                .setTitleText("Select Start Time")
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .build();

        startTimePicker.show(getParentFragmentManager(), "START_TIME_PICKER");

        startTimePicker.addOnPositiveButtonClickListener(v -> {
            int startHour = startTimePicker.getHour();
            int startMinute = startTimePicker.getMinute();

            String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);

            MaterialTimePicker endTimePicker = new MaterialTimePicker.Builder()
                    .setTitleText("Select End Time")
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(startHour)
                    .setMinute(startMinute)
                    .build();

            endTimePicker.show(getParentFragmentManager(), "END_TIME_PICKER");

            endTimePicker.addOnPositiveButtonClickListener(v2 -> {
                int endHour = endTimePicker.getHour();
                int endMinute = endTimePicker.getMinute();

                String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);

                timeEditText.setText(startTime + " - " + endTime);
            });
        });
    }
}
