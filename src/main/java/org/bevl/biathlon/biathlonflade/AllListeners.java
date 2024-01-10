package org.bevl.biathlon.biathlonflade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bevl.biathlon.biathlonflade.models.Country;
import org.bevl.biathlon.biathlonflade.models.ReloadPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.event.inventory.InventoryAction.*;

public class AllListeners implements Listener {

    Utli utli = new Utli();
    ShootingSystem shootingSystem = new ShootingSystem();

    ArrayList<String> Time = new ArrayList<String>();


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try {
            utli.setLoggedIn(event.getPlayer().getName(), false);
            Player player = event.getPlayer();

            player.setAllowFlight(true);
            if (utli.isRegistered(player.getName())) {
                event.setJoinMessage("Welcome!");

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://192.168.1.28:45456/user/country?&userName="+player.getName()))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


                        if(response.statusCode() == 200){

                            Gson gson = new GsonBuilder().create();

                    Country country = gson.fromJson(response.body(), Country.class);

                    player.setPlayerListName(country.character + player.getName());
                    player.saveData();
                    player.sendMessage(country.character + " " + country.name + " is your country!");
                            Bukkit.broadcastMessage(response.body());
                        }





                player.sendMessage("§aAuth §7>> §6Login | §4/login (Password)");
            } else {
                player.sendMessage("§aAuth §7>> §6Register on website www.fladeup.fun");
            }

        } catch (Exception exception){

        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        try {
            Player player = event.getPlayer();
            List<ReloadPlayer> reloadPlayerList = shootingSystem.reloadingPlayers.stream().filter(s -> s.player == player).collect(Collectors.toList());
            if(reloadPlayerList.size() > 0){
                ReloadPlayer reloadPlayer = reloadPlayerList.get(0);
                shootingSystem.reloadingPlayers.remove(reloadPlayer);
            }

        } catch (Exception exception){

        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
            player.sendMessage("§aAuth §7>> §6Login | §4/login (Passwort)");
            return;
        }
        else if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }

        if (Time.contains(player.getName())) {
            return;
        } else {
            Time.add(player.getName());
            Bukkit.getScheduler().runTaskLater(BiathlonFlade.plugin, new Runnable() {
                public void run() {
                    Time.remove(player.getName());
                }
            }, 50);
        }

        if (!utli.isRegistered(player.getName())) {
            player.sendMessage("§aAuth §7>> §6Register | §4/register (Passwort) (Passwort)");
            return;
        }
    }

    @EventHandler
    public void onPickUp(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickInventorySpec(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
            player.updateInventory();
        }
        else{
            if(event.getView().getTitle().contains("Biathlon items")){
                if(event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD || event.getAction() == HOTBAR_SWAP || event.getAction() == PICKUP_SOME || event.getAction() == PICKUP_ALL)
                {
                    event.setCancelled(true);
                    event.setResult(Event.Result.DENY);
                }
                player.getInventory().addItem(event.getCurrentItem());
            }

        }
    }

    @EventHandler
    public void onOpenInventorySpec(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!utli.isLoggedIn(player.getName())) {
                event.setCancelled(true);
            }
        }
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (!utli.isLoggedIn(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!event.getMessage().contains("login")
                && !event.getMessage().contains("register")
                && !event.getMessage().contains("unregister")
                && !event.getMessage().contains("logout")) {
            if (!utli.isLoggedIn(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();


        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
        else{
            Boolean isEnable = Boolean.parseBoolean(BiathlonFlade.plugin.getConfig().get("shootingSystemIsEnable").toString());
            if(isEnable)
                shootingSystem.ShoowAndReload(itemInHand, player, event);
        }


    }



    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event)
    {
        Arrow arrow = (Arrow) event.getProjectile();
        Player player = (Player) arrow.getShooter();
        Boolean isEnable = Boolean.parseBoolean(BiathlonFlade.plugin.getConfig().get("shootingSystemIsEnable").toString());


        if (!utli.isLoggedIn(player.getName())) {
            event.setCancelled(true);
        }
        else {
            if(!(event.getProjectile() instanceof Arrow)){
                return;
            }else if(isEnable){
                ItemStack bow = event.getBow();

                // Перевірка, чи маємо ми свою бескінечність
                if (bow.containsEnchantment(org.bukkit.enchantments.Enchantment.ARROW_INFINITE)) {
                    // Встановлюємо кількість стріл в нескінченність
                    ((Arrow) event.getProjectile()).setPickupStatus(org.bukkit.entity.AbstractArrow.PickupStatus.CREATIVE_ONLY);
                }
                player.updateInventory();
                event.setCancelled(true);
            }
        }



    }
}



