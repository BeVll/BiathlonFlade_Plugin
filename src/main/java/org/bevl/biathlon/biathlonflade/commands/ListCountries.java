package org.bevl.biathlon.biathlonflade.commands;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.util.EntityUtils;
import org.bevl.biathlon.biathlonflade.models.Country;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ListCountries implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://192.168.1.28:45456/country/all"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            if(response.statusCode() == 200){
                Gson gson = new GsonBuilder().create();

                Country[] countries = gson.fromJson(response.body(), Country[].class);

                for (int i = 0; i < countries.length; i++) {
                    sender.sendMessage("[" + countries[i].id + "] " + countries[i].character + countries[i].name);
                }
            }

            Bukkit.broadcastMessage(response.toString());
            System.out.print(response.toString());

        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
