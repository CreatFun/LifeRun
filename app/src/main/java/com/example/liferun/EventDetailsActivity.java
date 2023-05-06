package com.example.liferun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.liferun.model.Event;
import com.example.liferun.model.Note;

import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_EVENT = "EventDetailsActivity.EXTRA_EVENT";

    static Event event;

//    static Button pickDateButton;
    Button saveEventButton, cancelButton;

    private EditText eventName, eventDescription;
    private TextView createEdit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventName = findViewById(R.id.eventName);
        eventDescription = findViewById(R.id.eventDescription);
        createEdit_text = findViewById(R.id.createEdit_text);
        saveEventButton = findViewById(R.id.saveEventButton);
        cancelButton = findViewById(R.id.cancelButton);


        if (getIntent().hasExtra(EXTRA_EVENT)){
            event = getIntent().getParcelableExtra(EXTRA_EVENT);
            createEdit_text.setText("Редактирование события");
            eventName.setText(event.eventName);
            eventDescription.setText(event.eventDescription);
        }
        else {
            createEdit_text.setText("Создание события");
            event = new Event();
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventName.getText().length() > 0){
                    event.eventName = eventName.getText().toString();
                    event.eventDescription = eventDescription.getText().toString();
                    event.date = CalendarPage.pickedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
                    if (getIntent().hasExtra(EXTRA_EVENT)){
                        App.getInstance().getEventDao().update(event);
                    }
                    else {
                        App.getInstance().getEventDao().insert(event);
                    }

                }
                finish();
            }
        });

    }

    public static void startEventDetailsActivity(Activity caller, Event event){
        Intent intent = new Intent(caller, EventDetailsActivity.class);
        if (event != null){
            intent.putExtra(EXTRA_EVENT, event);
        }
        caller.startActivity(intent);
    }
}