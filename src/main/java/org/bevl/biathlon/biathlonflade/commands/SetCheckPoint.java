package org.bevl.biathlon.biathlonflade.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bevl.biathlon.biathlonflade.CheckPoints;
import org.bevl.biathlon.biathlonflade.models.CheckPoint;
import org.bevl.biathlon.biathlonflade.requests.checkpoint.CheckPointCreate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class SetCheckPoint implements CommandExecutor {
    CheckPoints checkPoints = new CheckPoints();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if(args.length != 9){
                sender.sendMessage("§3/setCheckPoint §6[trackId] [name] [type:start|checkpoint|finish|entrypenalty|exitpenalty|entryshoot|exitshoot] [x1] [y1] [z1] [x2] [y2] [z2]");
            }
            else{
                if(!Objects.equals(args[2], "start")
                        && !Objects.equals(args[2], "checkpoint")
                        && !Objects.equals(args[2], "finish")
                        && !Objects.equals(args[2], "entrypenalty")
                        && !Objects.equals(args[2], "exitpenalty")
                        && !Objects.equals(args[2], "entryshoot")
                        && !Objects.equals(args[2], "exitshoot")){
                    sender.sendMessage("§3/setCheckPoint §6[trackId] [name] [type:start|checkpoint|finish|entrypenalty|exitpenalty|entryshoot|exitshoot] [x1] [y1] [z1] [x2] [y2] [z2]");
                    return true;
                }

                CheckPointCreate checkPoint = new CheckPointCreate();

                switch (args[2]){
                    case "checkpoint":{
                        checkPoint.checkPointTypeId = 1;
                        break;
                    }
                    case "start":{
                        checkPoint.checkPointTypeId = 2;
                        break;
                    }
                    case "finish":{
                        checkPoint.checkPointTypeId = 3;
                        break;
                    }
                    case "entrypenalty":{
                        checkPoint.checkPointTypeId = 4;
                        break;
                    }
                    case "exitpenalty":{
                        checkPoint.checkPointTypeId = 5;
                        break;
                    }
                    case "entryshoot":{
                        checkPoint.checkPointTypeId = 6;
                        break;
                    }
                    case "exitshoot":{
                        checkPoint.checkPointTypeId = 7;
                        break;
                    }


                }

                checkPoint.trackId = Integer.parseInt(args[0]);
                checkPoint.name = args[1];
                checkPoint.x1 = Double.parseDouble(args[3]);
                checkPoint.y1 = Double.parseDouble(args[4]);
                checkPoint.z1 = Double.parseDouble(args[5]);
                checkPoint.x2 = Double.parseDouble(args[6]);
                checkPoint.y2 = Double.parseDouble(args[7]);
                checkPoint.z2 = Double.parseDouble(args[8]);

                checkPoints.updateCheckPoints();

                HttpClient client = HttpClient.newHttpClient();

                Gson gson = new GsonBuilder().create();
                String body = gson.toJson(checkPoint);
                Bukkit.getLogger().info(body);
                HttpRequest request = HttpRequest.newBuilder()
                        .header("Content-Type", "application/json")
                        .uri(URI.create("http://192.168.1.28:45456/api/checkPoints/create"))
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


                if(response.statusCode() == 200){

                    CheckPoint checkPointEntity = gson.fromJson(response.body(), CheckPoint.class);
                    String res = String.format("§2Created check point §3[%s] %s §2with vectors: §3%s %s %s AND %s %s %s", checkPointEntity.id, checkPointEntity.name, checkPointEntity.x1, checkPointEntity.y1, checkPointEntity.z1, checkPointEntity.x2, checkPointEntity.y2, checkPointEntity.z2);
                    Bukkit.getLogger().info(response.body());
                    sender.sendMessage(res);
                }
                Bukkit.getLogger().info(Integer.toString(response.statusCode()));
                Bukkit.getLogger().info(response.toString());


                return true;

            }

        } catch (Exception exception){

            Bukkit.getLogger().info(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
