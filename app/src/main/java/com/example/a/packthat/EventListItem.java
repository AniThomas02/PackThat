package com.example.a.packthat;

import java.io.Serializable;

/**
 * EventListItems hold the items inside lists inside events
 */
public class EventListItem implements Serializable {
    public int eliId;
    public String eliName, eliCompletedBy;

    public EventListItem(String eliName, int eliId, String eliCompletedBy) {
        this.eliName = eliName;
        this.eliId = eliId;
        this.eliCompletedBy = eliCompletedBy;
    }
}
