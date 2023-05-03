package com.example.liferun;

import android.app.Application;

import androidx.room.Room;

import com.example.liferun.data.AppDatabase;
import com.example.liferun.data.EventDao;
import com.example.liferun.data.NoteDao;

public class App extends Application {

    private AppDatabase database;
    private NoteDao noteDao;
    private EventDao eventDao;

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        database = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "app-db-name")
                .allowMainThreadQueries()
                .build();

        noteDao = database.noteDao();
        eventDao = database.eventDao();
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    public EventDao getEventDao(){
        return eventDao;
    }

    public void setEventDao(EventDao eventDao){
        this.eventDao = eventDao;
    }
}
