package org.bevl.biathlon.biathlonflade;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Utli {

    private static ArrayList<String> isLoggedinList = new ArrayList<String>();
    private File customConfigFile;
    private FileConfiguration customConfig;

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }
    // Encrypt method Verschlüsseln
    public void createCustomConfig() {
        File file = new File(BiathlonFlade.plugin.getDataFolder() + File.separator + "config.yml"); //This will get the config file


        if (!file.exists()){ //This will check if the file exist
            //Situation A, File doesn't exist

            BiathlonFlade.plugin.getConfig().addDefault("reloadingTime", "0.4"); //adding default settings
            BiathlonFlade.plugin.getConfig().addDefault("shootingSystemIsEnable", true);
            //Save the default settings
            BiathlonFlade.plugin.getConfig().options().copyDefaults(true);
            BiathlonFlade.plugin.saveConfig();
        } else {
            //situation B, Config does exist
            CheckConfig(); //function to check the important settings
            BiathlonFlade.plugin.saveConfig(); //saves the config
            BiathlonFlade.plugin.reloadConfig();    //reloads the config

        }
    }

    public void CheckConfig() {

        if(BiathlonFlade.plugin.getConfig().get("reloadingTime") == null){ //if the setting has been deleted it will be null
            BiathlonFlade.plugin.getConfig().set("reloadingTime", 0.2); //reset the setting
            BiathlonFlade.plugin.getConfig().set("shootingSystemIsEnable", true);
            BiathlonFlade.plugin.saveConfig();
            BiathlonFlade.plugin.reloadConfig();

        }

    }
    public boolean isLoggedIn(String name) {
        if (isLoggedinList.contains(name)) {
            return true;
        } else {
            return false;
        }
    }

    public void setLoggedIn(String name, boolean state) {
        if (isLoggedIn(name) && !state) {
            isLoggedinList.remove(name);
        }
        if (!isLoggedIn(name) && state) {
            isLoggedinList.add(name);
        }
    }

    public boolean isRegistered(String name) {


        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet post = new HttpGet("http://192.168.1.28:45456/auth/checkUserExist?userName=" + name);
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == 200) {
                return true;
            }
            else {
                return false;
            }

        } catch (Exception exception) {
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
            return false;
        }

    }

}