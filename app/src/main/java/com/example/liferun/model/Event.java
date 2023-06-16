package com.example.liferun.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Event implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "eventName")
    public String eventName;

    @ColumnInfo(name = "eventDescription")
    public String eventDescription;

    @ColumnInfo(name = "date")
    public long date;

    public Event(){

    }

    protected Event(Parcel in) {
        uid = in.readInt();
        eventName = in.readString();
        eventDescription = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(eventName);
        dest.writeString(eventDescription);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return uid == event.uid && Objects.equals(eventName, event.eventName) && Objects.equals(eventDescription, event.eventDescription) && Objects.equals(date, event.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, eventName, eventDescription, date);
    }
}
