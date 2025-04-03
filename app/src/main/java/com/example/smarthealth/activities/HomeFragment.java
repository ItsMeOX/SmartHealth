package com.example.smarthealth.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;
import com.example.smarthealth.bot_suggestions.BotSuggestion;
import com.example.smarthealth.bot_suggestions.BotSuggestionAdapter;
import com.example.smarthealth.bot_suggestions.BotSuggestionProvider;
import com.example.smarthealth.bot_suggestions.DatabaseBotSuggestionProvider;
import com.example.smarthealth.calendar.DatabaseCalendarEventProvider;
import com.example.smarthealth.calendar.CalendarEvent;
import com.example.smarthealth.calendar.CalendarEventProvider;
import com.example.smarthealth.calendar.CalendarAdapter;
import com.example.smarthealth.nutrient_intake.NutrientIntake;
import com.example.smarthealth.nutrient_intake.NutrientIntakeAdapter;
import com.example.smarthealth.nutrient_intake.NutrientIntakeProvider;
import com.example.smarthealth.nutrient_intake.DatabaseNutrientIntakeProvider;
import com.example.smarthealth.upcoming_schedule.DatabaseUpcomingScheduleProvider;
import com.example.smarthealth.upcoming_schedule.UpcomingSchedule;
import com.example.smarthealth.upcoming_schedule.UpcomingScheduleAdapter;
import com.example.smarthealth.upcoming_schedule.UpcomingScheduleProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements
        CalendarAdapter.OnItemListener,
        BotSuggestionAdapter.OnItemListener,
        CalendarFormFragment.NewEventCreatedListener
{

    private TextView monthYearText;
    private Calendar selectedDate;
    private RecyclerView calendarRecyclerView;
    private RecyclerView nutrientRecyclerView;
    private RecyclerView scheduleRecyclerView;
    private RecyclerView botSuggestionsRecyclerView;
    private CalendarEventProvider calendarEventProvider;
    private NutrientIntakeProvider nutrientIntakeProvider;
    private UpcomingScheduleProvider upcomingScheduleProvider;
    private BotSuggestionProvider botSuggestionProvider;
    private View view; // main view for this fragment

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);

        calendarEventProvider = new DatabaseCalendarEventProvider(requireContext());
        nutrientIntakeProvider = new DatabaseNutrientIntakeProvider();
        upcomingScheduleProvider = new DatabaseUpcomingScheduleProvider();
        botSuggestionProvider = new DatabaseBotSuggestionProvider();

        initCalendarWidgets();
        setUpMainContentSlider();
        initNutrientWidgets();
        initUpcomingScheduleWidgets();
        initBotSuggestionWidgets();

        selectedDate = Calendar.getInstance();
        setMonthView();
        setNutrientIntakeView();
        setUpcomingSchedules();
        setBotSuggestionsView();

        return view;
    }

    private void initCalendarWidgets() {
        LinearLayout calendarParentView = view.findViewById(R.id.calendarView);
        View calendarRecycleView = LayoutInflater.from(requireContext()).inflate(R.layout.calendar_view, calendarParentView, false);
        calendarParentView.addView(calendarRecycleView);
        calendarRecyclerView = (RecyclerView) view.findViewById(R.id.calendarRecyclerView);
        calendarRecyclerView.setNestedScrollingEnabled(false);
        monthYearText = (TextView) view.findViewById(R.id.calendarMonthYear);
    }

    private void setMonthView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        String selectedDateText = dateFormat.format(selectedDate.getTime());
        monthYearText.setText(selectedDateText);

        Pair<ArrayList<Calendar>, Integer> results = daysInMonthArray(selectedDate);
        ArrayList<Calendar> daysInMonth = results.first;
        int currentDatePosition = results.second;

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, calendarEventProvider, currentDatePosition, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private Pair<ArrayList<Calendar>, Integer> daysInMonthArray(Calendar selectedDate) {
        ArrayList<Calendar> daysInMonth = new ArrayList<>();
        selectedDate.set(Calendar.DAY_OF_MONTH, 1);
        int totalDaysInMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);

        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentDatePosition = -1;

        // Add days of previous months
        Calendar prevMonth = (Calendar) selectedDate.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int totalDaysPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < dayOfWeek; i++) {
            Calendar day = (Calendar) selectedDate.clone();
            day.set(Calendar.DAY_OF_MONTH, totalDaysPrevMonth - (6 - i));
            daysInMonth.add(day);
        }

        // Add days of the current month
        for (int day = 1; day <= totalDaysInMonth; day++) {
            Calendar dayCalendar = (Calendar) selectedDate.clone();
            dayCalendar.set(Calendar.DAY_OF_MONTH, day);
            daysInMonth.add(dayCalendar);

            if (day == currentDay) {
                currentDatePosition = daysInMonth.size() - 1;
            }
        }

        // Add empty strings for days after the last day of the month
        Calendar nextMonth = (Calendar) selectedDate.clone();
        nextMonth.add(Calendar.MONTH, 1);
        int totalSlots = 42;
        for (int day = 1; daysInMonth.size() < totalSlots; day++) {
            Calendar dayCalendar = (Calendar) nextMonth.clone();
            dayCalendar.set(Calendar.DAY_OF_MONTH, day);
            daysInMonth.add(dayCalendar);
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
        View mainContentView = view.findViewById(R.id.mainContentView);
        View calendarView = view.findViewById(R.id.calendarView);
        mainContentView.setOnTouchListener(new View.OnTouchListener() {
            private float distanceContentFinger; // distance from top of mainContent view to finger, used for repositioning of mainContent during dragging
            private float mainContentInitialY = -1; // initial y of main content, will be set only once after view initialization, used for repositioning of mainContent after dragging and limiting drag distance
            private float previousFingerY = -1; // previous finger y for dragging distance tracking
            private float currentFingerY = -1; // current finger y for dragging distance tracking
            private boolean isMoving = false; // is moving mainContent or not, is set to true when user finger down on top of mainContent view within moveThresholdY
            private float touchDownY = -1; // record of y of finger touch down for y translation of calendar
            private float initialTranslation = 0; // record of old y translation value of calendar before finger touch down

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // threshold distance from top of mainContent view to enable dragging
                float moveThresholdY = 200;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        // coordinate of mainContentView relative to screen
                        int[] mainContentViewCoord = new int[2];
                        mainContentView.getLocationOnScreen(mainContentViewCoord);
                        int mainContentViewY = mainContentViewCoord[1];

                        // If user drag the top part mainContentView, disable scrolling of page
                        if (event.getRawY() - mainContentViewY <= moveThresholdY) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }

                        // Getting originalY for resetting of mainContent after user slide up mainContent
                        if (mainContentInitialY == -1) {
                            mainContentView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mainContentInitialY = mainContentView.getY();
                                }
                            });
                        }

                        // Getting finger touchdownY for translation of calendar view
                        touchDownY = event.getRawY();

                        // Getting initialTranslation of calendar view for clamping of translation value
                        if (calendarRecyclerView.getChildCount() > 0) {
                            initialTranslation = calendarRecyclerView.getChildAt(0).getTranslationY();
                        }

                        // Getting position of mainContentView relative to finger position for repositioning according to finger position
                        distanceContentFinger = v.getY() - event.getRawY();

                        // Set isMoving to true if user drags mainContentView in designated area
                        if (event.getRawY() - mainContentViewY <= moveThresholdY) {
                            isMoving = true;
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (!isMoving) {
                            return true;
                        }

                        previousFingerY = currentFingerY;
                        currentFingerY = event.getRawY();

                        float mainContentNewY = event.getRawY() + distanceContentFinger;
                        float dyTranslationCalendar = event.getRawY() - touchDownY;
                        // Getting the first child to get y position value for clamping of y translation of calendar
                        int calendarFirstItemPosition = ((GridLayoutManager) calendarRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        View calendarFirstItemView = calendarRecyclerView.getLayoutManager().findViewByPosition(calendarFirstItemPosition);

                        if (mainContentNewY >= mainContentInitialY && mainContentNewY <= calendarView.getY() + calendarView.getHeight() + 30) {
                            // Move mainContent according to finger position
                            v.setY(mainContentNewY);
                            // Translate y position of each cells in calendar
                            if (calendarFirstItemView != null && (calendarFirstItemView.getY() < 0)) {
                                for (int i = 0; i < calendarRecyclerView.getChildCount(); i++) {
                                    View child = calendarRecyclerView.getChildAt(i);
                                    child.setTranslationY(Math.min(initialTranslation + dyTranslationCalendar, 0));  // Apply translation to each item in calendar
                                }
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        isMoving = false;

                        float topMost = mainContentInitialY;
                        float bottomMost = calendarView.getY() + calendarView.getHeight() + 30;
                        float swipeDistance = currentFingerY - previousFingerY;

                        // Set to position of mainContent to original position if user swipe up
                        // otherwise set to designated bottom position to expand calendar
                        if (swipeDistance > 0) {
                            // Set mainContent position
                            v.animate().y(bottomMost).setDuration(400).setInterpolator(new OvershootInterpolator()).start();

                            // Set calendar translation
                            for (int i = 0; i < calendarRecyclerView.getChildCount(); i++) {
                                View child = calendarRecyclerView.getChildAt(i);
                                child.setTranslationY(0);
                            }
                        } else {
                            // Set mainContent position
                            v.animate().y(topMost).setDuration(400).setInterpolator(new OvershootInterpolator()).start();

                            // Set calendar translation
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

    private void initNutrientWidgets() {
        nutrientRecyclerView = (RecyclerView) view.findViewById(R.id.nutrientIntakeRecyclerView);
    }

    private void setNutrientIntakeView() {
        List<NutrientIntake> nutrientIntakeList = nutrientIntakeProvider.getNutrientIntakes();

        NutrientIntakeAdapter nutrientIntakeAdapter = new NutrientIntakeAdapter(nutrientIntakeList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        nutrientRecyclerView.setLayoutManager(layoutManager);
        nutrientRecyclerView.setAdapter(nutrientIntakeAdapter);
        nutrientRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onCalenderCellClick(int position, List<Calendar> daysOfMonth) {
        Dialog calendarEventDialog = new Dialog(requireActivity());
        calendarEventDialog.setContentView(R.layout.calendar_event_popup);
        if (calendarEventDialog.getWindow() != null) {
            WindowManager.LayoutParams params = calendarEventDialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            calendarEventDialog.getWindow().setAttributes(params);
        }

        List<CalendarEvent> calendarEvents = calendarEventProvider.getEventsForDay((Calendar) Calendar.getInstance().clone());
        LinearLayout eventListContainer = calendarEventDialog.findViewById(R.id.calendarPopupItemContainer);

        TextView dateTextView = calendarEventDialog.findViewById(R.id.calendarPopupDate);

        SimpleDateFormat titleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        String selectedDateText = titleDateFormat.format(daysOfMonth.get(position).getTime());
        dateTextView.setText(selectedDateText);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        SimpleDateFormat eventTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        for (CalendarEvent calendarEvent : calendarEvents) {
            View eventView = inflater.inflate(R.layout.calendar_event_popup_item, eventListContainer, false);
            TextView eventTitleView = eventView.findViewById(R.id.calendarPopupEventTitle);
            TextView eventTimeView = eventView.findViewById(R.id.calendarPopupEventTime);
            TextView eventDescView = eventView.findViewById(R.id.calendarPopupEventDesc);

            eventTitleView.setText(calendarEvent.getEventTitle());
            eventTimeView.setText(eventTimeFormat.format(calendarEvent.getEventDateCalendar().first.getTime()));
            eventDescView.setText(calendarEvent.getEventDescription());
            eventListContainer.addView(eventView);
        }

        CardView eventAdder = (CardView) inflater.inflate(R.layout.calendar_event_popup_add, eventListContainer, false);
        eventListContainer.addView(eventAdder);

        eventAdder.setOnClickListener(v -> {
            CalendarFormFragment dialog = new CalendarFormFragment(eventListContainer);
            dialog.setOnCalendarEventCreated(this);
            dialog.show(getParentFragmentManager(), "CalendarFormDialog");
        });

        calendarEventDialog.show();
    }
    @Override
    public void onNewCalendarEventCreated(CalendarEvent event, LinearLayout eventListContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("CalendarFormDialog");
        Dialog dialog = fragment instanceof DialogFragment ? ((DialogFragment) fragment).getDialog() : null;

        if (dialog != null) {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            SimpleDateFormat eventTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            View eventView = inflater.inflate(R.layout.calendar_event_popup_item, eventListContainer, false);
            TextView eventTitleView = eventView.findViewById(R.id.calendarPopupEventTitle);
            TextView eventTimeView = eventView.findViewById(R.id.calendarPopupEventTime);
            TextView eventDescView = eventView.findViewById(R.id.calendarPopupEventDesc);

            eventTitleView.setText(event.getEventTitle());
            eventTimeView.setText(eventTimeFormat.format(event.getEventDateCalendar().first.getTime()));
            eventDescView.setText(event.getEventDescription());

            addToDatabase();

            eventListContainer.addView(eventView, eventListContainer.getChildCount()-1); // Add before "Add Event" button
        }
    }

    private void addToDatabase() {
        // TODO: to be done by Tristan.

    }

    private void initBotSuggestionWidgets() {
        botSuggestionsRecyclerView = view.findViewById(R.id.botSuggestionsRecyclerView);
    }

    public void setBotSuggestionsView() {
        List<BotSuggestion> botSuggestionsList = botSuggestionProvider.getBotSuggestions();

        BotSuggestionAdapter botSuggestionAdapter = new BotSuggestionAdapter(botSuggestionsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        botSuggestionsRecyclerView.setLayoutManager(layoutManager);
        botSuggestionsRecyclerView.setAdapter(botSuggestionAdapter);
        botSuggestionsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void initUpcomingScheduleWidgets() {
        scheduleRecyclerView = (RecyclerView) view.findViewById(R.id.upcomingScheduleRecyclerView);
    }

    public void setUpcomingSchedules() {
        List<UpcomingSchedule> upcomingSchedules = upcomingScheduleProvider.getTodaySchedules();

        UpcomingScheduleAdapter upcomingScheduleAdapter = new UpcomingScheduleAdapter(upcomingSchedules);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        scheduleRecyclerView.setLayoutManager(layoutManager);
        scheduleRecyclerView.setAdapter(upcomingScheduleAdapter);
    }

    public void onBotSuggestionClick(int position, List<BotSuggestion> botSuggestions) {
        BotSuggestion botSuggestion = botSuggestions.get(position);
        Dialog botSuggestionDialog = new Dialog(requireActivity());
        botSuggestionDialog.setContentView(R.layout.bot_suggestion_popup);
        if (botSuggestionDialog.getWindow() != null) {
            WindowManager.LayoutParams params = botSuggestionDialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            botSuggestionDialog.getWindow().setAttributes(params);
        }

        TextView popupTitleView = botSuggestionDialog.findViewById(R.id.suggestionPopupTitle);
        TextView popupDescView = botSuggestionDialog.findViewById(R.id.suggestionPopupDesc);
        popupTitleView.setText(botSuggestion.getTitle());
        popupDescView.setText(botSuggestion.getDescription());

        botSuggestionDialog.show();
    }


}
