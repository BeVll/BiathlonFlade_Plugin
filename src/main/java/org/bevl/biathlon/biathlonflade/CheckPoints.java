package org.bevl.biathlon.biathlonflade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bevl.biathlon.biathlonflade.models.CheckPoint;
import org.bevl.biathlon.biathlonflade.models.Country;
import org.bevl.biathlon.biathlonflade.models.PlayerCheckPoint;
import org.bevl.biathlon.biathlonflade.models.PlayerRace;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CheckPoints {
    private static ArrayList<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
    public static List<PlayerRace> playersRace = new ArrayList<>();
    private Map<Player, Long> lastTriggerTimeMap = new HashMap<>();
    private final long triggerCooldown = 2 * 1000;

    public static String convertToTimeFormat(long milliseconds) {
        // Розрахунок секунд і мілісекунд
        long seconds = milliseconds / 1000;
        long remainingMilliseconds = milliseconds % 1000;

        // Розрахунок хвилин і секунд
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        // Форматування часу у формат "mm:ss"
        String timeFormat = String.format("%02d:%02d", minutes, remainingSeconds);

        return timeFormat;
    }

    public void createCheckPointsFile() throws IOException {
        File file = new File(BiathlonFlade.plugin.getDataFolder() + File.separator + "checkpoints.json"); //This will get the config file


        if (!file.exists()) {
            Gson gson = new GsonBuilder().create();

            String jsonFile = gson.toJson(checkPoints);
            BufferedWriter writer = new BufferedWriter(new FileWriter(BiathlonFlade.plugin.getDataFolder() + File.separator + "checkpoints.json"));
            writer.write(jsonFile);
            writer.close();


        } else {
            BufferedReader br
                    = new BufferedReader(new FileReader(file));

            // Declaring a string variable
            String json = "";
            String st;
            while ((st = br.readLine()) != null)
                 json += st;
            Bukkit.getLogger().info(json);
            Gson gson = new GsonBuilder().create();

            CheckPoint[] fromJson = gson.fromJson(json, CheckPoint[].class);
            for (int i = 0; i < fromJson.length; i++) {
                checkPoints.add(fromJson[i]);
            }

            Bukkit.getLogger().info(Integer.toString(checkPoints.size()));
        }
    }

    public void updateCheckPointsFile() throws IOException {
        File file = new File(BiathlonFlade.plugin.getDataFolder() + File.separator + "checkpoints.json"); //This will get the config file


        Gson gson = new GsonBuilder().create();

        String jsonFile = gson.toJson(checkPoints);
        BufferedWriter writer = new BufferedWriter(new FileWriter(BiathlonFlade.plugin.getDataFolder() + File.separator + "checkpoints.json"));
        writer.write(jsonFile);
        writer.close();
    }

    public List<CheckPoint> getCheckPoints() {
        return checkPoints;
    }

    public void addCheckPoint(CheckPoint value) {
        Bukkit.getLogger().info(Integer.toString(checkPoints.size()));
        checkPoints.add(value);
    }

    public boolean isValueInRange(Double value, Double value1, Double value2) {
        if(value1 < value2){
            return value >= value1 && value <= value2;
        }
        else if(value1 > value2){
            return value >= value2 && value <= value1;
        }
        else {
            return value == value1;
        }
    }

    public void Check(Player player) {
        for (int i = 0; i < checkPoints.size(); i++) {
            CheckPoint cP = checkPoints.get(i);
            Double x = player.getLocation().getX();
            Double y = player.getLocation().getY();
            Double z = player.getLocation().getZ();

            if(isValueInRange(x, cP.X1, cP.X2) && isValueInRange(y, cP.Y1, cP.Y2) && isValueInRange(z, cP.Z1, cP.Z2)){
                long currentTime = System.currentTimeMillis();
                if (!lastTriggerTimeMap.containsKey(player) || (currentTime - lastTriggerTimeMap.get(player)) >= triggerCooldown) {
                    lastTriggerTimeMap.put(player, currentTime);

                    List<PlayerRace> playerRacesList = playersRace.stream().filter(r -> r.player == player).collect(Collectors.toList());
                    if(!playerRacesList.isEmpty()){
                        PlayerRace playerRace = playerRacesList.get(0);
                        if(Objects.equals(cP.type, "start")){
                            int index = playerRacesList.indexOf(playerRace);

                            playerRace.startTime = System.currentTimeMillis();
                            playerRacesList.set(index, playerRace);
                            player.sendMessage(String.format("§b[%s] §6%s", cP.name, player.getName()));

                        }
                        else if(Objects.equals(cP.type, "checkpoint")){
                            PlayerCheckPoint playerCheckPoint = new PlayerCheckPoint();
                            playerCheckPoint.Id = cP.Id;
                            playerCheckPoint.name = cP.name;
                            playerCheckPoint.X1 = cP.X1;
                            playerCheckPoint.Y1 = cP.Y1;
                            playerCheckPoint.Z1 = cP.Z1;
                            playerCheckPoint.X2 = cP.X2;
                            playerCheckPoint.Y2 = cP.Y2;
                            playerCheckPoint.Z2 = cP.Z2;
                            playerCheckPoint.resultTime = System.currentTimeMillis() - playerRace.startTime;
                            playerCheckPoint.type = cP.type;
                            int index = playerRacesList.indexOf(playerRace);
                            playerRacesList.set(index, playerRace);
                            player.sendMessage(String.format("§b[%s] §6%s §4%s", cP.name, player.getName(), convertToTimeFormat(playerCheckPoint.resultTime)));

                        }
                    }
                    else{
                        player.sendMessage(cP.type);
                        if(Objects.equals(cP.type, "start")){
                            player.sendMessage("1321");
                            PlayerRace playerRace = new PlayerRace();
                            playerRace.player = player;
                            playerRace.startTime = System.currentTimeMillis();
                            playerRace.checkPoints = new ArrayList<>();

                            playersRace.add(playerRace);
                            player.sendMessage(String.format("§b[%s] §6%s", cP.name, player.getName()));

                        }
                    }
                }

            }
        }
    }
}
