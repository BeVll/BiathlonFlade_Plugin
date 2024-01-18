package org.bevl.biathlon.biathlonflade.models;

import java.util.Date;

public class Event {
    public int id;
    public String eventName;
    public int eventTypeId;

    public Boolean teamEvent;
    public int trackId;

    public Date eventDate;

    public EventTypeEnitity eventType;
}
