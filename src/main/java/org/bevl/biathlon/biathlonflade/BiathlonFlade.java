package org.bevl.biathlon.biathlonflade;

import org.bevl.biathlon.biathlonflade.commands.BiatItems;
import org.bevl.biathlon.biathlonflade.commands.ListCountries;
import org.bevl.biathlon.biathlonflade.commands.Login;
import org.bevl.biathlon.biathlonflade.commands.SetCountry;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BiathlonFlade extends JavaPlugin {
    public static Plugin plugin;
    public final AllListeners allListeners = new AllListeners();
    @Override
    public void onEnable() {
        this.plugin = this;
        // Plugin startup logic
        Utli utli = new Utli();
        utli.createCustomConfig();
        this.getServer().getPluginManager().registerEvents(this.allListeners, this);
        this.getCommand("setCountry").setExecutor(new SetCountry());
        this.getCommand("listCountries").setExecutor(new ListCountries());
        this.getCommand("login").setExecutor(new Login());
        this.getCommand("biatItems").setExecutor(new BiatItems());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
