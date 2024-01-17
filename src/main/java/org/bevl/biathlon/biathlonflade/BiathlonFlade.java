package org.bevl.biathlon.biathlonflade;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bevl.biathlon.biathlonflade.commands.*;
import org.bevl.biathlon.biathlonflade.models.CheckPoint;
import org.bevl.biathlon.biathlonflade.models.Race;
import org.bevl.biathlon.biathlonflade.models.RaceSettings;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BiathlonFlade extends JavaPlugin {
    public static Plugin plugin;
    public static ProtocolManager protocolManager;
    public static RaceSettings raceSettings;
    public static Race race;
    public final AllListeners allListeners = new AllListeners();
    @Override
    public void onEnable() {
        this.plugin = this;
        // Plugin startup logic
        Utli utli = new Utli();
        race = new Race();
        raceSettings = new RaceSettings();
        CheckPoints checkPoints = new CheckPoints();
        utli.createCustomConfig();
        try {
            checkPoints.createCheckPointsFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("ProtocolLib not found! Disabling your plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else{
            protocolManager = ProtocolLibrary.getProtocolManager();
        }
        this.getServer().getPluginManager().registerEvents(this.allListeners, this);
        this.getCommand("setCountry").setExecutor(new SetCountry());
        this.getCommand("listCountries").setExecutor(new ListCountries());
        this.getCommand("login").setExecutor(new Login());
        this.getCommand("biatItems").setExecutor(new BiatItems());
        this.getCommand("setCheckPoint").setExecutor(new SetCheckPoint());
        this.getCommand("setPenalty").setExecutor(new SetPenalty());
        this.getCommand("shootTimePenalty").setExecutor(new ShootTimePenalty());
        this.getCommand("setLaps").setExecutor(new SetLaps());
        this.getCommand("additBullets").setExecutor(new SetLaps());
        this.getCommand("addStartList").setExecutor(new AddStartList());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
