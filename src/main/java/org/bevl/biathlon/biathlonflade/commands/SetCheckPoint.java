package org.bevl.biathlon.biathlonflade.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bevl.biathlon.biathlonflade.BiathlonFlade;
import org.bevl.biathlon.biathlonflade.CheckPoints;
import org.bevl.biathlon.biathlonflade.models.CheckPoint;
import org.bevl.biathlon.biathlonflade.models.Country;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SetCheckPoint implements CommandExecutor {
    CheckPoints checkPoints = new CheckPoints();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if(args.length != 9){
                sender.sendMessage("§3/setCheckPoint §6[trackId] [name] [type:start|checkpoint|finish|entryPenalty|exitPenalty] [x1] [y1] [z1] [x2] [y2] [z2]");
            }
            else{


                CheckPoint checkPoint = new CheckPoint();

                checkPoint.name = args[1];
                checkPoint.X1 = Double.parseDouble(args[2]);
                checkPoint.Y1 = Double.parseDouble(args[3]);
                checkPoint.Z1 = Double.parseDouble(args[4]);
                checkPoint.X2 = Double.parseDouble(args[5]);
                checkPoint.Y2 = Double.parseDouble(args[6]);
                checkPoint.Z2 = Double.parseDouble(args[7]);
                checkPoint.type = args[2];
                checkPoints.addCheckPoint(checkPoint);
                checkPoints.updateCheckPointsFile();
                String res = String.format("§2Created check point §3[%s] %s §2with vectors: §3%s %s %s AND %s %s %s", checkPoint.Id, checkPoint.name, checkPoint.X1, checkPoint.Y1, checkPoint.Z1, checkPoint.X2, checkPoint.Y2, checkPoint.Z2);
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
