package com.example.liferun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liferun.model.Event;
import com.example.liferun.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class EventPopup extends AppCompatActivity {

    TextView date;
    private RecyclerView recyclerView;
    FloatingActionButton addEventButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_events);

        date = findViewById(R.id.date);
        String dateText;
        dateText = String.valueOf(CalendarPage.pickedDate.getDayOfMonth())+CalendarPage.pickedDate.getMonth();
        date.setText(dateText);

        recyclerView = findViewById(R.id.eventsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        EventAdapter adapter = new EventAdapter();
        recyclerView.setAdapter(adapter);

        addEventButton = findViewById(R.id.addEventButton_popup);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailsActivity.startEventDetailsActivity(getParent(),null);
            }
        });

        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getEventLiveData().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                adapter.setItems(events);
            }
        });

    }


}
