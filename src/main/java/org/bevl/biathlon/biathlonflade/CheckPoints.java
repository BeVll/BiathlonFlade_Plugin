package org.bevl.biathlon.biathlonflade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bevl.biathlon.biathlonflade.models.CheckPoint;
import org.bevl.biathlon.biathlonflade.models.Country;
import org.bevl.biathlon.biathlonflade.models.PlayerCheckPoint;
import org.bevl.biathlon.biathlonflade.models.PlayerRace;
import org.bevl.biathlon.biathlonflade.requests.checkpoint.ResultCreate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class CheckPoints {
    private static List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
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

//    public void createCheckPointsFile() throws IOException {
//        File file = new File(BiathlonFlade.plugin.getDataFolder() + File.separator + "checkpoints.json"); //This will get the config file
//
//
//        if (!file.exists()) {
//            Gson gson = new GsonBuilder().create();
//
//            String jsonFile = gson.toJson(checkPoints);
//            BufferedWriter writer = new BufferedWriter(new FileWriter(BiathlonFlade.plugin.getDataFolder() + File.separator + "checkpoints.json"));
//            writer.write(jsonFile);
//            writer.close();
//
//
//        } else {
//            BufferedReader br
//                    = new BufferedReader(new FileReader(file));
//
//            // Declaring a string variable
//            String json = "";
//            String st;
//            while ((st = br.readLine()) != null)
//                 json += st;
//            Bukkit.getLogger().info(json);
//            Gson gson = new GsonBuilder().create();
//
//            CheckPoint[] fromJson = gson.fromJson(json, CheckPoint[].class);
//            for (int i = 0; i < fromJson.length; i++) {
//                checkPoints.add(fromJson[i]);
//            }
//
//            Bukkit.getLogger().info(Integer.toString(checkPoints.size()));
//        }
//    }

    public void updateCheckPoints() {
        try{
            HttpClient client = HttpClient.newHttpClient();

            Gson gson = new GsonBuilder().create();

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .uri(URI.create("http://192.168.1.28:45456/api/checkPoints/all"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            if(response.statusCode() == 200){

                CheckPoint[] checkPointEntity = gson.fromJson(response.body(), CheckPoint[].class);
                checkPoints = Arrays.stream(checkPointEntity).toList();
                Bukkit.getLogger().info(response.body());

            }
        } catch (Exception ex){

        }
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
        try {
            for (int i = 0; i < checkPoints.size(); i++) {
                CheckPoint cP = checkPoints.get(i);
                Double x = player.getLocation().getX();
                Double y = player.getLocation().getY();
                Double z = player.getLocation().getZ();

                if(isValueInRange(x, cP.x1, cP.x2) && isValueInRange(y, cP.y1, cP.y2) && isValueInRange(z, cP.z1, cP.z2)){
                    long currentTime = System.currentTimeMillis();
                    if (!lastTriggerTimeMap.containsKey(player) || (currentTime - lastTriggerTimeMap.get(player)) >= triggerCooldown) {
                        lastTriggerTimeMap.put(player, currentTime);

                        List<PlayerRace> playerRacesList = playersRace.stream().filter(r -> r.player == player).collect(Collectors.toList());
                        if(!playerRacesList.isEmpty()){
                            PlayerRace playerRace = playerRacesList.get(0);

                            ResultCreate resultCreate = new ResultCreate();
                            resultCreate.raceId = BiathlonFlade.race.Id;
                            resultCreate.userName = player.getName();
                            resultCreate.checkPointId = cP.id;
                            resultCreate.resultValue = System.currentTimeMillis() - playerRace.startTime;
                            resultCreate.isDNF = false;
                            resultCreate.startNumber = 1;
                            resultCreate.isTeamResult = false;
                            resultCreate.isFinish = false;
                            resultCreate.lap = playerRace.currentLap;

                            player.sendMessage(String.format("§b[%s] §6%s §4%s", cP.name, player.getName(), convertToTimeFormat(resultCreate.resultValue)));

                            if(cP.checkPointTypeId == 1){
                                int index = playerRacesList.indexOf(playerRace);
                                playerRacesList.set(index, playerRace);


                            }
                            else if(cP.checkPointTypeId == 2){
                                int index = playerRacesList.indexOf(playerRace);
                                playerRace.startTime = System.currentTimeMillis();
                                playerRace.currentLap = 1;
                                playerRacesList.set(index, playerRace);
                                player.sendMessage(String.format("§b[%s] §6%s", cP.name, player.getName()));

                            }

                            else if(cP.checkPointTypeId == 3){

                                int index = playerRacesList.indexOf(playerRace);
                                playerRacesList.set(index, playerRace);
                                player.sendMessage(String.format("§b[%s] §6%s §4%s", cP.name, player.getName(), convertToTimeFormat(resultCreate.resultValue)));

                            }

                            if(BiathlonFlade.race.isGoing && cP.checkPointTypeId != 2){
                                HttpClient client = HttpClient.newHttpClient();

                                Gson gson = new GsonBuilder().create();
                                String body = gson.toJson(resultCreate);
                                Bukkit.getLogger().info(body);
                                HttpRequest request = HttpRequest.newBuilder()
                                        .header("Content-Type", "application/json")
                                        .uri(URI.create("http://192.168.1.28:45456/api/results/create"))
                                        .POST(HttpRequest.BodyPublishers.ofString(body))
                                        .build();
                                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


                                if(response.statusCode() == 200){
                                    ResultCreate res = gson.fromJson(response.body(), ResultCreate.class);
                                    Bukkit.getLogger().info(response.body());
                                }
                            }
                        }
                        else{

                            if(cP.checkPointTypeId == 2){
                                player.sendMessage("1321");
                                PlayerRace playerRace = new PlayerRace();
                                playerRace.player = player;
                                playerRace.startTime = System.currentTimeMillis();
                                playerRace.checkPoints = new ArrayList<>();
                                playerRace.currentLap = 1;
                                playersRace.add(playerRace);
                                player.sendMessage(String.format("§b[%s] §6%s", cP.name, player.getName()));

                            }
                        }

                    }

                }
            }
        } catch (Exception ex){
            Bukkit.getLogger().info(ex.toString());
        }
    }
}
