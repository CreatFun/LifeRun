package com.example.liferun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.liferun.model.Event;
import com.example.liferun.model.Note;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_EVENT = "EventDetailsActivity.EXTRA_EVENT";

    static Event event;

    static Button pickDateButton;
    public static long currentDate;
    Button saveEventButton, cancelButton;

    private EditText eventName, eventDescription;
    private TextView createEdit_text, eventDate_text;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Используем текущую выбранную дату как дату по умолчанию
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(currentDate);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Calendar cal = Calendar.getInstance();
            cal.set(year,month,day,0,0,0);

            event.date = cal.getTimeInMillis()/1000 * 1000; // устанавливаем миллисекунды в 000, иначе событие не найдется в EventAdapter
            Locale locale = new Locale("ru");
            pickDateButton.setText(String.format(locale,"%1$d  %2$s", cal.get(Calendar.DATE), cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)));
        }

    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new EventDetailsActivity.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventName = findViewById(R.id.eventName);
        eventDescription = findViewById(R.id.eventDescription);
        createEdit_text = findViewById(R.id.createEdit_text);
        eventDate_text = findViewById(R.id.eventDate_text);
        saveEventButton = findViewById(R.id.saveEventButton);
        cancelButton = findViewById(R.id.cancelButton);
        pickDateButton = findViewById(R.id.pickDateButton);

        // Определение выбранной даты в long формате
        currentDate = CalendarPage.pickedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()*1000;

        // Вписываем текущую дату в заголовок
        String dateText;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM");
        dateText = CalendarPage.pickedDate.format(formatter);
        eventDate_text.setText(CalendarPage.capitalizeFirstLetter(dateText));


        if (getIntent().hasExtra(EXTRA_EVENT)){
            event = getIntent().getParcelableExtra(EXTRA_EVENT);
            createEdit_text.setText("Редактирование события");
            eventName.setText(event.eventName);
            eventDescription.setText(event.eventDescription);
        }
        else {
            createEdit_text.setText("Создание события");
            event = new Event();
            // если событие создается, по умолчанию ставим дату выбранного дня
            // домножаем на 1000 чтобы получить миллисекунды
            event.date = currentDate;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(event.date);
        Log.i("date", String.valueOf(cal.get(Calendar.DATE)));
        Log.i("display name", String.valueOf(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru"))));
        Locale locale = new Locale("ru");
        String dateButtonText = String.format(locale, "%1$d  %2$s", cal.get(Calendar.DATE), cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));
        pickDateButton.setText(dateButtonText);

        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

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