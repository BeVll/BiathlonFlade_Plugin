package org.bevl.biathlon.biathlonflade.commands;

import org.bevl.biathlon.biathlonflade.BiathlonFlade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;

public class AddStartList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        String[] players = args;
        if(args.length < 1){
            sender.sendMessage("§4/addStartList §6[players...]");
            return false;
        }

        try {
            for (int i = 0; i < players.length; i++) {
                Player p = BiathlonFlade.plugin.getServer().getPlayer(players[i]);
                if(p == null){
                    sender.sendMessage("§4Player " + players[i] + " not found");
                    return false;
                }
                BiathlonFlade.race.startList.add(p);

            }

            ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
            final Objective objective = scoreboard.registerNewObjective("test", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.RED + "Start list");
            // Створюємо команду для гравців

            int totalTimeInSeconds = 0;
            DecimalFormat df = new DecimalFormat("00");
            // Додаємо гравців до команди
            for (int i = 0; i < BiathlonFlade.race.startList.size(); i++) {
                int minutes = totalTimeInSeconds / 60;
                int seconds = totalTimeInSeconds % 60;
                String formattedTime = df.format(minutes) + ":" + df.format(seconds);

                Score s = objective.getScore("§4" + (i+1) + ".§6" +BiathlonFlade.race.startList.get(i).getName() + " §c" + formattedTime);
                s.setScore(BiathlonFlade.race.startList.size()-i);

                totalTimeInSeconds += 30;
            }

            // Встановлюємо scoreboard гравцям
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setScoreboard(scoreboard);

                Bukkit.getLogger().info("SetScoreboard " + player.getName());

            }
            sender.sendMessage("§2Players added to start list");


        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
