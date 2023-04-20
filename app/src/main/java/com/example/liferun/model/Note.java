package com.example.liferun.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Note implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "noteName")
    public String noteName;

    @ColumnInfo(name = "noteDescription")
    public String noteDescription;

    @ColumnInfo(name = "timestamp")
    public long timestamp;

    @ColumnInfo(name = "deadlineDate")
    public long deadlineDate;

    @ColumnInfo(name = "done")
    public boolean done;

    public Note(){

    }

    protected Note(Parcel in) {
        uid = in.readInt();
        noteName = in.readString();
        noteDescription = in.readString();
        timestamp = in.readLong();
        deadlineDate = in.readLong();
        done = in.readByte() != 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return uid == note.uid && timestamp == note.timestamp && deadlineDate == note.deadlineDate && done == note.done && Objects.equals(noteName, note.noteName) && Objects.equals(noteDescription, note.noteDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, noteName, noteDescription, timestamp, deadlineDate, done);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(uid);
        parcel.writeString(noteName);
        parcel.writeString(noteDescription);
        parcel.writeLong(timestamp);
        parcel.writeLong(deadlineDate);
        parcel.writeByte((byte) (done ? 1 : 0));
    }
}
