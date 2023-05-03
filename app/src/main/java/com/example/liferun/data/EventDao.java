package com.example.liferun.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.liferun.model.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM Event")
    List<Event> getAll();

    @Query("SELECT * FROM Event")
    LiveData<List<Event>> getAllLiveData();

    @Query("SELECT * FROM Event WHERE uid IN (:eventIds)")
    List<Event> loadAllByIds(int[] eventIds);

    @Query("SELECT * FROM Event WHERE uid = :uid LIMIT 1")
    Event findById(int uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);
}
