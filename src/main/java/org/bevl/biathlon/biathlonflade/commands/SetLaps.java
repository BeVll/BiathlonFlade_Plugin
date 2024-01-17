package org.bevl.biathlon.biathlonflade.commands;

import org.bevl.biathlon.biathlonflade.BiathlonFlade;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetLaps implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        Integer count = Integer.parseInt(args[0]);
        if(args.length != 1){
            sender.sendMessage("§4/setLaps §6[count]");
            return false;
        }

        try {


            BiathlonFlade.raceSettings.LapsCount = count;
            sender.sendMessage("§2Race laps count is " + count + " laps");


        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
