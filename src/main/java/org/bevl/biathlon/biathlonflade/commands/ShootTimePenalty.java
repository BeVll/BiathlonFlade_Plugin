package org.bevl.biathlon.biathlonflade.commands;

import org.bevl.biathlon.biathlonflade.BiathlonFlade;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShootTimePenalty implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        Integer seconds = Integer.parseInt(args[0]);
        if(args.length != 1){
            sender.sendMessage("§4/shootTimePenalty §6[seconds]");
            return false;
        }

        try {
            double d = seconds * 1000.0;

            BiathlonFlade.raceSettings.ShootingTimePenalty = d;
            sender.sendMessage("§2Shooting penalty time is " + seconds + " seconds");


        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
