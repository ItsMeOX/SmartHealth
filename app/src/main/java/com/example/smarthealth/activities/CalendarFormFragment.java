package com.example.smarthealth.activities;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.example.smarthealth.R;
import com.example.smarthealth.api_service.EventDto;
import com.example.smarthealth.api_service.EventService;
import com.example.smarthealth.api_service.RetrofitClient;
import com.example.smarthealth.calendar.CalendarEvent;
import com.example.smarthealth.calendar.CalendarUtil;
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

public class CalendarFormFragment extends DialogFragment {
    private TextInputEditText dateEditText;
    private TextInputEditText timeEditText;
    private TextInputLayout timeInputLayout;
    private TextInputLayout dateInputLayout;
    View view;
    private NewEventCreatedListener listener;
    private final String dateFormat = "yyyy/MM/dd";
    private final LinearLayout eventListContainer;
    private SharedPreferences sharedPreferences;
    private EventService eventService;
    private long userId;

    public CalendarFormFragment(LinearLayout eventListContainer) {
        this.eventListContainer = eventListContainer;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.calendar_form_fragment, container, false);

        eventService = RetrofitClient.getInstance().create(EventService.class);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getLong("userId", -1);

        Button closeButton = view.findViewById(R.id.closeButton);
        dateEditText = view.findViewById(R.id.eventDateEditText);
        timeEditText = view.findViewById(R.id.eventTimeEditText);
        Button submitButton = view.findViewById(R.id.eventAddSubmitButton);
        timeInputLayout = view.findViewById(R.id.eventTimeTextField);
        dateInputLayout = view.findViewById(R.id.eventDateTextField);

        dateEditText.setOnClickListener(v -> openDatePicker(dateEditText));
        timeEditText.setOnClickListener(v -> openTimePicker(timeEditText));
        submitButton.setOnClickListener(v -> onSubmit());

        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent); // Ensure no background padding
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
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

    private void onSubmit() {
        TextInputLayout nameInputLayout = view.findViewById(R.id.eventNameTextField);
        TextInputLayout descpInputLayout = view.findViewById(R.id.eventDescpTextField);
        TextInputEditText nameEditText = view.findViewById(R.id.eventNameEditText);
        TextInputEditText descpEditText = view.findViewById(R.id.eventDescpEditText);

        String eventName = nameEditText.getText().toString();
        String eventDescp = descpEditText.getText().toString();
        String eventDateRange = dateEditText.getText().toString();
        String eventTimeRange = timeEditText.getText().toString();

        boolean hasError = false;

        nameInputLayout.setErrorEnabled(false);
        descpInputLayout.setErrorEnabled(false);
        dateInputLayout.setErrorEnabled(false);
        timeInputLayout.setErrorEnabled(false);

        if (eventName.isEmpty()) {
            nameInputLayout.setError("Event name cannot be empty.");
            hasError = true;
        }
        if (eventDescp.isEmpty()) {
            descpInputLayout.setError("Event description cannot be empty.");
            hasError = true;
        }
        if (eventDateRange.isEmpty()) {
            dateInputLayout.setError("Event date cannot be empty.");
            hasError = true;
        }
        if (eventTimeRange.isEmpty()) {
            timeInputLayout.setError("Event time cannot be empty.");
            hasError = true;
        }

        if (hasError) {
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

            // UTC+8 for Singapore
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

            if (listener != null) {
                CalendarEvent calendarEvent = new CalendarEvent(
                        eventName,
                        eventDescp,
                        startDate,
                        endDate
                );
                listener.onNewCalendarEventCreated(calendarEvent, eventListContainer);
                addToDatabase(calendarEvent);
                dismiss();
            }

        } catch (ParseException | NumberFormatException e) {
            dateInputLayout.setError("Wrong date or time format");
            return;
        }
    }

    private void addToDatabase(CalendarEvent calendarEvent) {
        EventDto eventDto = new EventDto(
                calendarEvent.getEventTitle(),
                calendarEvent.getEventDescription(),
                calendarEvent.getEventStartCalendar(),
                calendarEvent.getEventEndCalendar()
        );

        Call<EventDto> call = eventService.createEvent(userId, eventDto);
        call.enqueue(new Callback<EventDto>() {
            @Override
            public void onResponse(Call<EventDto> call, Response<EventDto> response) {
                if(response.isSuccessful() && response.body() != null){
                    Log.d("debug", "Add calendar event successfully");
                }
            }
            @Override
            public void onFailure(Call<EventDto> call, Throwable t) {
                Log.d("debug", "error: " + t.getMessage());
            }
        });
    }

    public void setOnCalendarEventCreated(NewEventCreatedListener listener) {
        this.listener = listener;
    }

    public interface NewEventCreatedListener {
        void onNewCalendarEventCreated(CalendarEvent event, LinearLayout eventListContainer);
    }
}
