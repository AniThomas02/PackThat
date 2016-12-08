package com.example.a.packthat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The lists within events
 */
public class EventList implements Serializable {
    public int eventListId;
    public String eventListName;
    public ArrayList<EventListItem> eventListItems;

    public EventList(int eventListId, String eventListName) {
        this.eventListId = eventListId;
        this.eventListName = eventListName;
        eventListItems = new ArrayList<>();
    }
}
