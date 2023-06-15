package com.example.liferun;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.liferun.model.Note;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Locale;

public class NoteDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE = "NoteDetailsActivity.EXTRA_NOTE";

    static Note note;

    static Button pickDateButton;
    Button saveNoteButton;
    Button backButton;

    private EditText noteName;
    private EditText noteDescription;
    private TextView createEdit_text;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(requireContext(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            Calendar cal = Calendar.getInstance();
            cal.set(year,month,day,23,59,59);
            note.deadlineDate = cal.getTimeInMillis();
            Locale locale = new Locale("ru");
            pickDateButton.setText(String.format(locale,"%1$d  %2$s", cal.get(Calendar.DATE), cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale)));
        }



    }


    public static void start(Activity caller, Note note){
        Intent intent = new Intent(caller, NoteDetailsActivity.class);
        if (note != null){
            intent.putExtra(EXTRA_NOTE, note);
        }
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        noteName = findViewById(R.id.noteName);
        noteDescription = findViewById(R.id.noteDescription);
        pickDateButton = findViewById(R.id.pickDateButton);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        backButton = findViewById(R.id.backButton);
        createEdit_text = findViewById(R.id.createEdit_text);

        if (getIntent().hasExtra(EXTRA_NOTE)){
            note = getIntent().getParcelableExtra(EXTRA_NOTE);
            noteName.setText(note.noteName);
            noteDescription.setText(note.noteDescription);
            createEdit_text.setText("Редактирование задачи");

            if (note.deadlineDate != 0L) {
                createEdit_text.setText("Создание задачи");
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(note.deadlineDate);
                Locale locale = new Locale("ru");
                String dateButtonText = String.format(locale, "%1$d  %2$s", cal.get(Calendar.DATE), cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale));
                pickDateButton.setText(dateButtonText);
            }
        }
        else {
            note = new Note();
        }



        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // должно возвращать к списку заметок
            }
        });

        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noteName.getText().length() > 0){
                    note.noteName = noteName.getText().toString();
                    note.noteDescription = noteDescription.getText().toString();
                    note.done = false;
                    note.timestamp = System.currentTimeMillis();
                    if (getIntent().hasExtra(EXTRA_NOTE)){
                        App.getInstance().getNoteDao().update(note);
                    }
                    else {
                        App.getInstance().getNoteDao().insert(note);
                    }

                }
                finish();
            }
        });





    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void startSettingsActivity(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}


