package org.bevl.biathlon.biathlonflade.commands;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bevl.biathlon.biathlonflade.Utli;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Login implements CommandExecutor {
    Utli utli = new Utli();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String password = args[0];
        Player player = sender.getServer().getPlayer(sender.getName());
        try {
            List<NameValuePair> urlParameters = new ArrayList<>();

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://192.168.1.28:45456/auth/login?userName="+player.getName()+"&password="+password);
            HttpResponse response = client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200){
                utli.setLoggedIn(sender.getName(), true);
                sender.sendMessage("§aAuth §7>> §aYou are logged!");
            }

            Bukkit.broadcastMessage(response.toString());
            System.out.print(response.toString());
            return true;
        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
            return false;
        }
    }
}
