package com.example.liferun;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.liferun.model.Event;
import com.example.liferun.model.Note;

import java.util.List;

public class MainViewModel extends ViewModel {
    private LiveData<List<Note>> noteLiveData = App.getInstance().getNoteDao().getAllLiveData();

    public LiveData<List<Note>> getNoteLiveData() {
        return noteLiveData;
    }

    private LiveData<List<Event>> eventLiveData = App.getInstance().getEventDao().getAllLiveData();

    public LiveData<List<Event>> getEventLiveData(){
        return eventLiveData;
    }
}
