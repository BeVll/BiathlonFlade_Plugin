package org.bevl.biathlon.biathlonflade.commands;

import org.bevl.biathlon.biathlonflade.CheckPoints;
import org.bevl.biathlon.biathlonflade.models.CheckPoint;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetStartPoint implements CommandExecutor {
    CheckPoints checkPoints = new CheckPoints();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if(args.length != 8){
                sender.sendMessage("§3/setStartPoint §6[id] [name] [x1] [y1] [z1] [x2] [y2] [z2]");
            }
            else{
                List<CheckPoint> checkPointsList = checkPoints.getCheckPoints();
                CheckPoint checkPoint = new CheckPoint();
                checkPoint.Id = Integer.parseInt(args[0]);
                checkPoint.name = args[1];
                checkPoint.X1 = Double.parseDouble(args[2]);
                checkPoint.Y1 = Double.parseDouble(args[3]);
                checkPoint.Z1 = Double.parseDouble(args[4]);
                checkPoint.X2 = Double.parseDouble(args[5]);
                checkPoint.Y2 = Double.parseDouble(args[6]);
                checkPoint.Z2 = Double.parseDouble(args[7]);
                checkPoint.type = "start";
                checkPoints.addCheckPoint(checkPoint);
                checkPoints.updateCheckPointsFile();
                String res = String.format("§2Created start point §3[%s] %s §2with vectors: §3%s %s %s AND %s %s %s", checkPoint.Id, checkPoint.name, checkPoint.X1, checkPoint.Y1, checkPoint.Z1, checkPoint.X2, checkPoint.Y2, checkPoint.Z2);
                sender.sendMessage(res);
                return true;

            }

        } catch (Exception exception){
            Bukkit.getLogger().info(exception.getMessage());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
