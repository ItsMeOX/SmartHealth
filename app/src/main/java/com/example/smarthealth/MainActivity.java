package com.example.smarthealth;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Calendar selectedDate;
    private LinearLayout scheduleContainer;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);


        initCalendarWidgets();
        setUpMainContentSlider();
        initUpcomingScheduleWidgets();
        initBotSuggestionWidgets();

        selectedDate = Calendar.getInstance();
        setMonthView();
    }

    private void initCalendarWidgets() {
        LinearLayout calendarParentView = findViewById(R.id.calendarView);
        View calendarRecycleView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.calendar_view, calendarParentView, false);
        calendarParentView.addView(calendarRecycleView);

        calendarRecyclerView = (RecyclerView) findViewById(R.id.calendarRecyclerView);
        monthYearText = (TextView) findViewById(R.id.calendarMonthYear);
    }

    private void setMonthView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        String selectedDateText = dateFormat.format(selectedDate.getTime());
        monthYearText.setText(selectedDateText);

        Pair<ArrayList<String>, Integer> results = daysInMonthArray(selectedDate);
        ArrayList<String> daysInMonth = results.first;
        int currentDatePosition = results.second;
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, currentDatePosition, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);

        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private Pair<ArrayList<String>, Integer> daysInMonthArray(Calendar selectedDate) {
        ArrayList<String> daysInMonth = new ArrayList<>();
        selectedDate.set(Calendar.DAY_OF_MONTH, 1);
        int totalDaysInMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);

        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentDatePosition = -1;

        // Add days of previous months
        selectedDate.add(Calendar.MONTH, -1);
        int totalDaysPrevMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        selectedDate.add(Calendar.MONTH, 1);
        for (int i = 1; i < dayOfWeek; i++) {
            daysInMonth.add(String.valueOf(totalDaysPrevMonth - (6 - i)));
        }

        // Add days of the current month
        for (int day = 1; day <= totalDaysInMonth; day++) {
            daysInMonth.add(String.valueOf(day));

            if (day == currentDay) {
                currentDatePosition = daysInMonth.size() - 1;
            }
        }

        // Add empty strings for days after the last day of the month
        int totalSlots = 42;
        for (int day = 1; daysInMonth.size() < totalSlots; day++) {
            daysInMonth.add(String.valueOf(day));
        }

        return new Pair<>(daysInMonth, currentDatePosition);
    }


    public void previousMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, -1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, 1);
        setMonthView();
    }

    private void setUpMainContentSlider() {
        View mainContentView = findViewById(R.id.mainContentView);
        View calendarView = findViewById(R.id.calendarView);
        mainContentView.setOnTouchListener(new View.OnTouchListener() {
            private float dY;
            private float originalY = -1;
            private final float moveThreshY = 200;
            private float previousY = -1;
            private float currentY = -1;
            private boolean isMoving = false;
            private float touchDownY = -1;
            private float initialTranslation = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getRawY() - mainContentView.getY() <= moveThreshY) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (originalY == -1) {
                            mainContentView.post(new Runnable() {
                                @Override
                                public void run() {
                                    originalY = mainContentView.getY();
                                }
                            });
                        }

                        touchDownY = event.getRawY();
                        if (calendarRecyclerView.getChildCount() > 0) {
                            initialTranslation = calendarRecyclerView.getChildAt(0).getTranslationY();
                        }
                        dY = v.getY() - event.getRawY();
                        if (event.getRawY() - mainContentView.getY() <= moveThreshY) {
                            isMoving = true;
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (!isMoving) {
                            return true;
                        }
                        float newY = event.getRawY() + dY;
                        float dyTranslation = event.getRawY() - touchDownY;
                        previousY = currentY;
                        currentY = event.getRawY();
                        int calendarFirstItemPosition = ((GridLayoutManager) calendarRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        View calendarFirstItemView = calendarRecyclerView.getLayoutManager().findViewByPosition(calendarFirstItemPosition);
                        if (newY >= originalY && newY <= calendarView.getY() + calendarView.getHeight() + 30) {
                            v.setY(newY);
                            if (calendarFirstItemView != null && (calendarFirstItemView.getY() < 0)) {
                                for (int i = 0; i < calendarRecyclerView.getChildCount(); i++) {
                                    View child = calendarRecyclerView.getChildAt(i);
                                    child.setTranslationY(Math.min(initialTranslation + dyTranslation, 0));  // Apply translation to each item
                                }
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        isMoving = false;

                        float topMost = originalY;
                        float bottomMost = calendarView.getY() + calendarView.getHeight() + 30;
                        float swipeDistance = currentY - previousY;

                        if (swipeDistance > 0) {
                            v.animate().y(bottomMost).setDuration(400).setInterpolator(new OvershootInterpolator()).start();
                            for (int i = 0; i < calendarRecyclerView.getChildCount(); i++) {
                                View child = calendarRecyclerView.getChildAt(i);
                                child.setTranslationY(0);
                            }
                        } else {
                            v.animate().y(topMost).setDuration(400).setInterpolator(new OvershootInterpolator()).start();
                            Calendar calendar = Calendar.getInstance();
                            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            int currentDateRow = (currentDay + calendar.get(Calendar.DAY_OF_WEEK) - 2) / 7;
                            for (int i = 0; i < calendarRecyclerView.getChildCount(); i++) {
                                View child = calendarRecyclerView.getChildAt(i);
                                child.setTranslationY(-child.getHeight() * currentDateRow);
                            }
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onItemClick(int position, String dayText) {
        // TODO: link to Android Calendar? and complete this function.
        String message = "Selected date" + dayText;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initBotSuggestionWidgets() {
        // TODO: Replace placeholders with suggestion from AI (pls set word limits)

        String[] titles = {"Increase Protein Intake", "Choose complex carbohydrates", "Stay hydrated"};
        String[] descriptions = {"Increase in take of fish, chicken, eggs, tofu, legumes to...",
                "Choose complex carbohydrates for example whole grains, vegetables to avoid sugar spikes. Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao." +
                        "Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.",
                "Drink more water, or herbal teas, soups to stay hydrated, because H2O make you human."};

        for (int i = 0; i < titles.length; i++) {
            addBotSuggestion(titles[i], descriptions[i]);
        }
    }

    public void addBotSuggestion(String title, String description) {
        LinearLayout suggestionContainer = findViewById(R.id.botSuggestionsBox);
        LayoutInflater inflater = LayoutInflater.from(this);
        View suggestionView = inflater.inflate(R.layout.bot_suggestion_view, suggestionContainer, false);

        TextView titleView = suggestionView.findViewById(R.id.suggestionTitle);
        TextView descView = suggestionView.findViewById(R.id.suggestionDesc);

        titleView.setText(title);
        descView.setText(description);

        suggestionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a popup view for user to view full text (if text is too long)
                Dialog botSuggestionDialog = new Dialog(MainActivity.this);
                botSuggestionDialog.setContentView(R.layout.bot_suggestion_popup);
                if (botSuggestionDialog.getWindow() != null) {
                    WindowManager.LayoutParams params = botSuggestionDialog.getWindow().getAttributes();
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    botSuggestionDialog.getWindow().setAttributes(params);
                }

                TextView popupTitleView = botSuggestionDialog.findViewById(R.id.suggestionPopupTitle);
                TextView popupDescView = botSuggestionDialog.findViewById(R.id.suggestionPopupDesc);
                popupTitleView.setText(title);
                popupDescView.setText(description);

                botSuggestionDialog.show();
            }
        });

        suggestionContainer.addView(suggestionView);
    }

    private void initUpcomingScheduleWidgets() {
        // TODO: fetch schedule from database.

        scheduleContainer = findViewById(R.id.upcomingScheduleLayout);
        addSchedule("Aspirin", "Today", "12:00pm", R.drawable.medicine);
        addSchedule("Lunch", "Today", "12:30pm", R.drawable.meal);
        addSchedule("Nospirit", "Today", "01:00pm", R.drawable.medicine);
    }

    public void addSchedule(String scheduleName, String scheduleDay, String scheduleTime, int iconResId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View scheduleView = inflater.inflate(R.layout.upcoming_schedule_view, scheduleContainer, false);

        TextView scheduleNameView = scheduleView.findViewById(R.id.upcomingScheduleName);
        ImageView scheduleIconView = scheduleView.findViewById(R.id.upcomingScheduleIcon);
        TextView scheduleDayView = scheduleView.findViewById(R.id.upcomingScheduleDay);
        TextView scheduleTimeView = scheduleView.findViewById(R.id.upcomingScheduleTime);

        scheduleNameView.setText(scheduleName);
        scheduleDayView.setText(scheduleDay);
        scheduleTimeView.setText(scheduleTime);

        scheduleIconView.setImageResource(iconResId);
        scheduleContainer.addView(scheduleView);
    }

}
