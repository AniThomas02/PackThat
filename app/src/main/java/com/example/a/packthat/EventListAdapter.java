package com.example.a.packthat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A custom list adapter to handle lists of events
 */
public class EventListAdapter extends ArrayAdapter<Event> {
    public EventListAdapter(Context context, int resource, ArrayList<Event> eventsList) {
        super(context, resource, eventsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_events, parent, false);
        }
        TextView friendName = (TextView) convertView.findViewById(R.id.textView_event_list_name);
        friendName.setText(event.name);
        TextView friendEmail = (TextView) convertView.findViewById(R.id.textView_event_list_startDate);
        friendEmail.setText(event.startDate);

        return convertView;
    }
}
