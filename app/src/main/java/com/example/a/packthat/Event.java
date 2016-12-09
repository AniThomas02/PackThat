package com.example.a.packthat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The data of an event.
 */
public class Event implements Serializable {
    public int id, createdById, isPrivate;
    public String name, description, startDate;
    public ArrayList<EventList> eventLists;

    public Event(int id, int createdById, String name, String description, String startDate, int isPrivate) {
        this.id = id;
        this.createdById = createdById;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.isPrivate = isPrivate;
        eventLists = new ArrayList<>();
    }
}
