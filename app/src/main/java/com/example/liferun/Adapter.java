package com.example.liferun;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.liferun.model.Note;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.annotations.NonNull;

public class Adapter extends RecyclerView.Adapter<Adapter.NoteViewHolder> {

    private SortedList<Note> sortedList;

    public Adapter() {

        sortedList = new SortedList<>(Note.class, new SortedList.Callback<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                if (!o2.done && o1.done) {
                    return 1;
                }
                if (o2.done && !o1.done) {
                    return -1;
                }
                if (o2.deadlineDate == 0L & o1.deadlineDate != 0L){
                    return -1;
                }
                if (o2.deadlineDate != 0L & o1.deadlineDate == 0L){
                    return 1;
                }
                if (o2.deadlineDate<o1.deadlineDate){
                    return 1;
                }
                if (o2.deadlineDate>o1.deadlineDate){
                    return -1;
                }
                return (int) (o2.timestamp - o1.timestamp);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Note oldItem, Note newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Note item1, Note item2) {
                return item1.uid == item2.uid;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @androidx.annotation.NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_list, parent, false));
    }


    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull NoteViewHolder holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void setItems(List<Note> notes){
        sortedList.replaceAll(notes);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView noteText, noteDeadline;
        CheckBox completed;
        View delete;

        Note note;

        boolean silentUpdate;


        public NoteViewHolder(@NonNull View itemView){
            super(itemView);
            noteText = itemView.findViewById(R.id.note_text);
            noteDeadline = itemView.findViewById(R.id.note_deadline);
            completed = itemView.findViewById(R.id.completed);
            delete = itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NoteDetailsActivity.start((Activity) itemView.getContext(), note);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.getInstance().getNoteDao().delete(note);
                }
            });

            completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (!silentUpdate){
                        note.done = checked;
                        App.getInstance().getNoteDao().update(note);
                    }
                    updateStrokeOut();
                }
            });
        }

        public void bind(Note note){
            this.note = note;

            noteText.setText(note.noteName);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(note.deadlineDate);
            int day = cal.get(Calendar.DATE);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);

            if (note.deadlineDate != 0L){
                noteDeadline.setText(String.format(Locale.ENGLISH, "%1$d.%2$d.%3$d", day, month, year));
                updateStrokeOut();
                updateTextColor();
            }
            else noteDeadline.setText("");


            silentUpdate = true;
            completed.setChecked(note.done);
            silentUpdate = false;
        }

        private void updateStrokeOut(){
            if (note.done){
                noteText.setPaintFlags(noteText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                updateTextColor();
            }
            else {
                noteText.setPaintFlags(noteText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                updateTextColor();

            }
        }

        private void updateTextColor(){
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DATE);
            cal.set(year,month,day,0,0,0);
            long currentTime = cal.getTimeInMillis();
            if (note.done){
                noteText.setTextColor(Color.LTGRAY);
            }
            else{
                if (note.deadlineDate != 0L & note.deadlineDate < currentTime){
                    noteText.setTextColor(Color.RED);
                }
                else{
                    noteText.setTextColor(Color.BLACK);
                }
            }

        }
    }
}
