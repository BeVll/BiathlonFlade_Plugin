package org.bevl.biathlon.biathlonflade.models;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Race {
    public int Id;
    public Event event;
    public List<String> startList;
    public boolean isGoing;

    public Race(){
        startList = new ArrayList<String>();
    }
}
