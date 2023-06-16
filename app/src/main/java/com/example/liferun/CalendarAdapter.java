package com.example.liferun;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.liferun.model.Event;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> daysOfMonth;
    private final OnItemListener onItemListener;
    SortedList<Event> sortedList;

    public CalendarAdapter(ArrayList<LocalDate> daysOfMonth, OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;

        sortedList = new SortedList<>(Event.class, new SortedList.Callback<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Event oldItem, Event newItem) {
                return false;
            }

            @Override
            public boolean areItemsTheSame(Event item1, Event item2) {
                return false;
            }

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        });
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener, daysOfMonth);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        LocalDate date = daysOfMonth.get(position);
        if (date != null){
            holder.calendarCell.setBackgroundColor(Color.parseColor("#A4AAAF"));
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if (CalendarPage.selectedDate.equals(LocalDate.now())
                    & date.getDayOfMonth() == LocalDate.now().getDayOfMonth()){
                holder.dayOfMonth.setTextColor(Color.BLUE);
                holder.dayOfMonth.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            }
            if (CalendarPage.pickedDate != null){
                if (CalendarPage.pickedDate.equals(date)){
                    holder.calendarCell.setPadding(2,2,2,2);
                    holder.calendarCell.setBackgroundColor(Color.parseColor("#3C5A76"));
                }
            }
            if (sortedList.size() != 0){
                for (int i=0; i < sortedList.size();i++){
                    if (sortedList.get(i).date == date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()*1000){
                        holder.eventIndicator.setImageResource(R.drawable.event_indicator);
                    }
                }
            }
        }
        else holder.dayOfMonth.setText("");


    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }

    public void setItems(List<Event> events){
        sortedList.clear();
        if (events != null){
            for (Event event:
                    events) {
                sortedList.add(event);
            }
        }
    }
}

