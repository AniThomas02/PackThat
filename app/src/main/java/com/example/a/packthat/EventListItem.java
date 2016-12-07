package com.example.a.packthat;

import java.io.Serializable;

/**
 * Created by Ani Thomas on 12/5/2016.
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
