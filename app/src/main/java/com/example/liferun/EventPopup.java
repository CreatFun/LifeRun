package com.example.liferun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liferun.model.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventPopup extends AppCompatActivity {

    TextView date;
    FloatingActionButton addEventButton;

    boolean isUpdated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_events);

        updateEventsList();


    }



    @Override
    protected void onResume() {
        super.onResume();
        // заставляет снова обновить список, например после добавления новой заметки
        isUpdated = false;

    }

    private void updateEventsList() {
        date = findViewById(R.id.date);
        String dateText;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM");
        dateText = CalendarPage.pickedDate.format(formatter);
//        dateText = String.valueOf(CalendarPage.pickedDate.getDayOfMonth())+CalendarPage.pickedDate.getMonth();
        date.setText(dateText);

        RecyclerView recyclerView = findViewById(R.id.eventsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        EventAdapter adapter = new EventAdapter();
        recyclerView.setAdapter(adapter);

        addEventButton = findViewById(R.id.addEventButton_popup);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailsActivity.startEventDetailsActivity(EventPopup.this,null);
            }
        });

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getEventLiveData().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                adapter.setItems(events);
                // повторно обновляет страницу, чтобы список отобразился сразу
                if (!isUpdated){
                    updateEventsList();
                    isUpdated = true;
                }
                //
            }
        });
    }

    public static void startEventPopupActivity(Activity caller){
        Intent intent = new Intent(caller, EventPopup.class);

        caller.startActivity(intent);
    }


}
