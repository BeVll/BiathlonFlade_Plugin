package org.bevl.biathlon.biathlonflade;

import org.bevl.biathlon.biathlonflade.commands.*;
import org.bevl.biathlon.biathlonflade.models.CheckPoint;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BiathlonFlade extends JavaPlugin {
    public static Plugin plugin;

    public final AllListeners allListeners = new AllListeners();
    @Override
    public void onEnable() {
        this.plugin = this;
        // Plugin startup logic
        Utli utli = new Utli();
        CheckPoints checkPoints = new CheckPoints();
        utli.createCustomConfig();
        try {
            checkPoints.createCheckPointsFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getServer().getPluginManager().registerEvents(this.allListeners, this);
        this.getCommand("setCountry").setExecutor(new SetCountry());
        this.getCommand("listCountries").setExecutor(new ListCountries());
        this.getCommand("login").setExecutor(new Login());
        this.getCommand("biatItems").setExecutor(new BiatItems());
        this.getCommand("setCheckPoint").setExecutor(new SetCheckPoint());
        this.getCommand("setStartPoint").setExecutor(new SetStartPoint());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
