package com.example.liferun;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> daysOfMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> daysOfMonth, OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
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
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if (CalendarPage.selectedDate.equals(LocalDate.now())
                    & date.getDayOfMonth() == LocalDate.now().getDayOfMonth()){
                holder.dayOfMonth.setTextColor(Color.BLUE);
                holder.dayOfMonth.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG);
            }
            if (CalendarPage.pickedDate != null){
                if (CalendarPage.pickedDate.equals(date)){
                    holder.calendarCell.setBackgroundColor(Color.parseColor("#B0C7E8"));
                }
            }
//        else{
//            holder.dayOfMonth.setTextColor(Color.BLACK);
//        }
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
}

