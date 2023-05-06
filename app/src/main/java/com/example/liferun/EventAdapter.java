package com.example.liferun;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.liferun.model.Event;
import com.example.liferun.model.Note;

import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    SortedList<Event> sortedList;

    //TODO: add events sorting
    public EventAdapter(){
        sortedList = new SortedList<>(Event.class, new SortedList.Callback<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return 0;
            }

            @Override
            public void onChanged(int position, int count) {

            }

            @Override
            public boolean areContentsTheSame(Event oldItem, Event newItem) {
                return false;
            }

            @Override
            public boolean areItemsTheSame(Event item1, Event item2) {
                return false;
            }

            @Override
            public void onInserted(int position, int count) {

            }

            @Override
            public void onRemoved(int position, int count) {

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {

            }
        });
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EventViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void setItems(List<Event> events){
        long currentDate = CalendarPage.pickedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
        sortedList.clear();
        for (Event event:
             events) {
            long eventDate = event.date;
            if (eventDate == currentDate){
                sortedList.add(event);
            }
        }
//        sortedList.replaceAll(events);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder{

        TextView eventTime, eventName;

        Event event;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTime = itemView.findViewById(R.id.event_time);
            eventName = itemView.findViewById(R.id.event_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventDetailsActivity.startEventDetailsActivity((Activity) itemView.getContext(),event);
                }
            });
        }

        public void bind(Event event){
            this.event = event;

            eventName.setText(event.eventName);
            //TODO: set event time
            eventTime.setText("00:00");
        }
    }
}
