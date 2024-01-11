package org.bevl.biathlon.biathlonflade.models;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Timer;

public class PlayerRace {
    public Player player;
    public List<PlayerCheckPoint> checkPoints;
    public long startTime;
    public int currentLap;
}
