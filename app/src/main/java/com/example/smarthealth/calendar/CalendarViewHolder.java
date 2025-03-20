package com.example.smarthealth.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.Calendar;
import java.util.List;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
    public final View currentDayMarker;
    public final View hasEventMarker;
    private final CalendarAdapter.OnItemListener onItemListener;
    private final List<Calendar> daysOfMonth;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, List<Calendar> daysOfMonth) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        currentDayMarker = itemView.findViewById(R.id.currentDayMarker);
        hasEventMarker = itemView.findViewById(R.id.cellDayMarker);
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onCalenderCellClick(getAdapterPosition(), daysOfMonth);
    }
}
