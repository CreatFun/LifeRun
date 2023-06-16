package com.example.liferun;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.liferun.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class CalendarPage extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    public static LocalDate selectedDate, pickedDate;

    FloatingActionButton addEventButton;

    List<Event> events;

    boolean isUpdated = false;
    View v;



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
        v = inflater.inflate(R.layout.fragment_calendar_page, container, false);

        initWidgets(v);
        selectedDate = LocalDate.now();
        pickedDate = selectedDate;
        setMonthView(v);

        addEventButton = v.findViewById(R.id.addEventButton_page);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailsActivity.startEventDetailsActivity(getActivity(),null);
            }
        });

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getEventLiveData().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {

            }
        });
        events = mainViewModel.getEventLiveData().getValue();


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // заставляет снова обновить список, например после добавления новой заметки
        isUpdated = false;

    }

    private void initWidgets(View v)
    {
        calendarRecyclerView = v.findViewById(R.id.calendarRecyclerView);
        monthYearText = v.findViewById(R.id.monthYearTV);

        ImageButton previousMonth = v.findViewById(R.id.previousMonthButton);
        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMonthAction(v);
            }
        });
        ImageButton nextMonth = v.findViewById(R.id.nextMonthButton);
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

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getEventLiveData().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                calendarAdapter.setItems(events);
                // повторно обновляет страницу, чтобы список отобразился сразу
                if (!isUpdated){
                    setMonthView(v);
                    isUpdated = true;
                }
                //
            }
        });
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
            // обновление календаря, обновление выделения клетки
            monthYearText.setText(monthYearFromDate(selectedDate));
            ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectedDate);

            CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
            calendarRecyclerView.setAdapter(calendarAdapter);


            MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            mainViewModel.getEventLiveData().observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
                @Override
                public void onChanged(List<Event> events) {
                    calendarAdapter.setItems(events);
                    // повторно обновляет страницу, чтобы список отобразился сразу
                    if (!isUpdated){
                        setMonthView(v);
                        isUpdated = true;
                    }
                }
            });
            events = mainViewModel.getEventLiveData().getValue();
            for (Event event:
                    events) {
                long eventDate = event.date;
                // Домножаем pickedDate чтобы получить время в миллисекундах
                if (eventDate == pickedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()*1000){
                    startEventPopup();
                    break;
                }
            }
        }
    }

    public static String capitalizeFirstLetter(String inputString) {
        char firstLetter = inputString.charAt(0);
        char capitalFirstLetter = Character.toUpperCase(firstLetter);
        return inputString.replace(inputString.charAt(0), capitalFirstLetter);
    }


    public void startEventPopup(){
        EventPopup.startEventPopupActivity(getActivity());
    }
}