package org.bevl.biathlon.biathlonflade.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bevl.biathlon.biathlonflade.BiathlonFlade;
import org.bevl.biathlon.biathlonflade.models.CheckPoint;
import org.bevl.biathlon.biathlonflade.models.Event;
import org.bevl.biathlon.biathlonflade.models.Race;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Random;
import java.util.TimerTask;

public class StartRace implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = sender.getServer().getPlayer(sender.getName());
        Integer isEnabled = Integer.parseInt(args[0]);
        if(args.length != 1){
            sender.sendMessage("§4/startRace §6[raceId]");
            return false;
        }

        try {

            Location initiatorLocation = player.getLocation();
            int id = Integer.parseInt(args[0]);

            HttpClient client = HttpClient.newHttpClient();

            Gson gson = new GsonBuilder().create();

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .uri(URI.create("http://192.168.1.28:45456/api/events/"+id))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            if(response.statusCode() == 200){

                Event event = gson.fromJson(response.body(), Event.class);

                BiathlonFlade.race.Id = event.id;
                BiathlonFlade.race.event = event;
                BiathlonFlade.race.isGoing = false;

                Bukkit.getLogger().info(response.body());

                for (Player playerOnline : Bukkit.getOnlinePlayers()) {

                    playerOnline.sendTitle("Current race: " + event.eventName, "");
                }
                new org.bevl.biathlon.biathlonflade.models.StartTimer(initiatorLocation, player).runTaskTimer(BiathlonFlade.plugin, 0L, 20L);
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        BiathlonFlade.race.isGoing = true;
                    }
                };

                task.runTaskLater(BiathlonFlade.plugin, 100L);
            }



        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
