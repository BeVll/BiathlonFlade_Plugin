package org.bevl.biathlon.biathlonflade.commands;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetCountry implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);

        Integer countryId = Integer.parseInt(args[1]);
        if(args.length != 2){
            sender.sendMessage("§4/setCountry §6[player] [country_id]");
            return false;
        }

        try {
            List<NameValuePair> urlParameters = new ArrayList<>();

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://192.168.1.28:45456/user/updateCountry?countryId="+countryId+"&userName="+target.getName());
            HttpResponse response = client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8").replace("\"", "");

                target.setPlayerListName(responseString + target.getName());
                target.saveData();
                Bukkit.broadcastMessage(responseString);
            }
            else if(response.getStatusLine().getStatusCode() == 701){
                sender.sendMessage("§4Country not found!");
                sender.sendMessage("§4/setCountry §6[player] [country_id]");
                return false;
            }
            else if(response.getStatusLine().getStatusCode() == 404){
                sender.sendMessage("§4Player not found!");
                sender.sendMessage("§4/setCountry §6[player] [country_id]");
                return false;
            }
            else{
                sender.sendMessage("§4/setCountry §6[player] [country_id]");
                return false;
            }
            target.saveData();

            Bukkit.broadcastMessage(response.toString());
            System.out.print(response.toString());

        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
