package com.example.smarthealth;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private final int currentDatePosition;
    private final Calendar selectedDate;

    public CalendarAdapter(ArrayList<String> daysOfMonth, int currentDatePosition, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.currentDatePosition = currentDatePosition;
        this.onItemListener = onItemListener;
        selectedDate = Calendar.getInstance();
    }

    private int getFirstDayIndex() {
        int currentDay = selectedDate.get(Calendar.DAY_OF_MONTH);
        selectedDate.set(Calendar.DAY_OF_MONTH, 1);
        int index = selectedDate.get(Calendar.DAY_OF_WEEK);
        selectedDate.set(Calendar.DAY_OF_MONTH, currentDay);

        return index;
    }

    private int getLastDayIndex() {
        return getFirstDayIndex()-2 + selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent ,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.16666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        holder.dayOfMonth.setText(daysOfMonth.get(position));

        // Set text to gray for days of previous and next month
        if (position < getFirstDayIndex()-1 || position > getLastDayIndex()) {
            holder.dayOfMonth.setTextColor(Color.parseColor("#8C8C8C"));
        }

        holder.itemView.post(new Runnable() {
            @Override
            public void run() {
                // Transition Y up to show only current week
                holder.itemView.setTranslationY(-holder.itemView.getHeight() * (float)(currentDatePosition/7));
            }
        });

        // TODO: link events to Android calendar system
        holder.hasEventMarker.setVisibility(View.INVISIBLE);
        if (position != currentDatePosition) {
            holder.currentDayMarker.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
