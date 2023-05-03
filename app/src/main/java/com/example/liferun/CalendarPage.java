package com.example.liferun;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class CalendarPage extends Fragment implements CalendarAdapter.OnItemListener {

    PopupWindow popupWindow;

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    public static LocalDate selectedDate, pickedDate;

    private Button previousMonth, nextMonth;

    FloatingActionButton addEventButton;



    public CalendarPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar_page, container, false);

        initWidgets(v);
        selectedDate = LocalDate.now();
        pickedDate = selectedDate;
        setMonthView(v);

        addEventButton = v.findViewById(R.id.addEventButton_page);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: add event adding
            }
        });


        return v;
    }


    private void initWidgets(View v)
    {
        calendarRecyclerView = v.findViewById(R.id.calendarRecyclerView);
        monthYearText = v.findViewById(R.id.monthYearTV);

        previousMonth = v.findViewById(R.id.previousMonthButton);
        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonthAction(v);
            }
        });
        nextMonth = v.findViewById(R.id.nextMonthButton);
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMonthAction(v);
            }
        });
    }

    private void setMonthView(View v)
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<LocalDate> daysInMonthArray(LocalDate date)
    {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1;

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add(null);
            }
            else
            {
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL yyyy");
        return capitalizeFirstLetter(date.format(formatter));
    }

    public void previousMonthAction(View view)
    {
        selectedDate = selectedDate.minusMonths(1);
        if (selectedDate.equals(LocalDate.now())){
            pickedDate = selectedDate;
        }
        else pickedDate = selectedDate.withDayOfMonth(1);
        setMonthView(view);
    }

    public void nextMonthAction(View view)
    {
        selectedDate = selectedDate.plusMonths(1);
        if (selectedDate.equals(LocalDate.now())){
            pickedDate = selectedDate;
        }
        else pickedDate = selectedDate.withDayOfMonth(1);
        setMonthView(view);
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        if (date != null){
            pickedDate = date;
            monthYearText.setText(monthYearFromDate(selectedDate));
            ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectedDate);

            CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
            calendarRecyclerView.setAdapter(calendarAdapter);
            startEventPopup();
        }
    }

    public static String capitalizeFirstLetter(String inputString) {
        char firstLetter = inputString.charAt(0);
        char capitalFirstLetter = Character.toUpperCase(firstLetter);
        return inputString.replace(inputString.charAt(0), capitalFirstLetter);
    }

    public void showEvents(){

    }

    public void startEventPopup(){
        Intent intent = new Intent(getActivity(), EventPopup.class);
        startActivity(intent);
    }
}