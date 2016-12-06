package com.example.a.packthat;

import java.io.Serializable;

/**
 * Created by Ani Thomas on 12/5/2016.
 */
public class EventListItem implements Serializable {
    public int eliId, eliCompletedBy;
    public String eliName;

    public EventListItem(String eliName, int eliId, int eliCompletedBy) {
        this.eliName = eliName;
        this.eliId = eliId;
        this.eliCompletedBy = eliCompletedBy;
    }
}
