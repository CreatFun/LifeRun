package com.example.liferun.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.liferun.model.Event;
import com.example.liferun.model.Note;

@Database(entities = {Note.class, Event.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();

    public abstract EventDao eventDao();
}
