package org.bevl.biathlon.biathlonflade.commands;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.bevl.biathlon.biathlonflade.BiathlonFlade;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetPenalty implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        Integer isEnabled = Integer.parseInt(args[0]);
        if(args.length != 1){
            sender.sendMessage("§4/setPenalty §6[1-true|0-false]");
            return false;
        }

        try {
            if(isEnabled == 1){
                BiathlonFlade.raceSettings.IsPenaltyLapEnabled = true;
                sender.sendMessage("§2Penalty laps is enabled");
            }
            else if(isEnabled == 0){
                BiathlonFlade.raceSettings.IsPenaltyLapEnabled = false;
                sender.sendMessage("§2Penalty laps is disabled");
            }
            else{
                sender.sendMessage("§4/setPenalty §6[1-true|0-false]");
                return false;
            }

        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
