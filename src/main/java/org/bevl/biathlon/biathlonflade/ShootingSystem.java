package org.bevl.biathlon.biathlonflade;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;


import org.bevl.biathlon.biathlonflade.models.ReloadPlayer;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class ShootingSystem {
    private double maxArrowDistance = 0.5;
    public static List<ReloadPlayer> reloadingPlayers = new ArrayList<>();
    private final Random random = new Random();
    private Map<Player, BukkitRunnable> crosshairTasks = new HashMap<>();

    public void simulateCrosshairJitter(Player player) {
        if (crosshairTasks.containsKey(player)) {
            crosshairTasks.get(player).cancel();
        }

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                Random random = new Random();
                float currentYaw = player.getLocation().getYaw();
                float currentPitch = player.getLocation().getPitch();
                double randomYaw = random.nextDouble() * 6-3.0; // Випадковий кут огляду
                double randomPitch = random.nextDouble() * 6-3.0; // Випадковий кут нагнуття
                currentYaw = currentYaw + Float.parseFloat(Double.toString(randomYaw));
                currentPitch = currentPitch + Float.parseFloat(Double.toString(randomPitch));
                Location l = player.getLocation();
                l.setPitch(currentPitch);
                l.setYaw(currentYaw);
                // Застосовуємо новий кут огляду гравця
                player.teleport(l);
                player.sendMessage("change");


            }
        };

        task.runTaskTimer(BiathlonFlade.plugin, 0, 16);
        crosshairTasks.put(player, task);
    }

    public void removeCrosshairJitter(Player player) {
        if (crosshairTasks.containsKey(player)) {
            crosshairTasks.get(player).cancel();
            crosshairTasks.remove(player);
        }
    }

    public boolean arrowHitWoodenButton(Arrow arrow) {
        Location arrowLocation = arrow.getLocation();
        Material buttonMaterial = arrowLocation.getBlock().getType();

        // Check if the block hit by the arrow is a wooden button
        if (buttonMaterial == Material.ACACIA_BUTTON) {
            // Check if the arrow is close enough to the button
            double distance = arrowLocation.distance(arrowLocation.getBlock().getLocation().add(0.5, 0.5, 0.5));
            Bukkit.getLogger().info(Double.toString(distance));
            return distance <= 1.0;
        }

        return false;
    }

    public boolean ArrowHitButton(Location arrowLocation, Location buttonLocation, ProjectileHitEvent event) {
        double distance = arrowLocation.distance(buttonLocation);
        Bukkit.broadcastMessage(Double.toString(distance));
        if(distance > maxArrowDistance){
            event.setCancelled(true);
        }
        return distance <= maxArrowDistance;
    }

    public void ShoowAndReload(ItemStack itemInHand, Player player, PlayerInteractEvent event){
        if (itemInHand != null
                && itemInHand.getType() == Material.BOW
                && event.getAction().toString().contains("RIGHT")
                && player.getInventory().contains(Material.ARROW)) {
            List<ReloadPlayer> reloadPlayerList = reloadingPlayers.stream().filter(s -> s.player == player).collect(Collectors.toList());
            if(reloadPlayerList.size() == 0){
                ReloadPlayer reloadPlayer = new ReloadPlayer();
                reloadPlayer.isReloaded = true;
                reloadPlayer.isReloading = false;
                reloadPlayer.player = player;
                reloadingPlayers.add(reloadPlayer);
                reloadPlayerList = reloadingPlayers.stream().filter(s -> s.player == player).collect(Collectors.toList());
            }
            ReloadPlayer reloadPlayer = reloadPlayerList.get(0);

            if(!reloadPlayer.isReloaded){
                player.sendMessage("§4Reload please!");
            }
            else{
                Arrow arrow = player.launchProjectile(Arrow.class);


                arrow.setGravity(false);
                arrow.setShooter(player);

                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
                arrow.setVelocity(player.getLocation().getDirection().multiply(6D));
                arrow.getVelocity().setY(0);
                arrow.setFireTicks(15);
                removeArrow(player);
                reloadPlayer.isReloaded = false;
                int index = reloadingPlayers.indexOf(reloadPlayer);
                reloadingPlayers.set(index, reloadPlayer);
            }

        }
        if (itemInHand != null
                && itemInHand.getType() == Material.BOW
                && event.getAction().toString().contains("LEFT")
                && player.getInventory().contains(Material.ARROW)) {
            // Your custom logic here
            player.sendMessage("§4Reloading...");
            List<ReloadPlayer> reloadPlayer = reloadingPlayers.stream().filter(s -> s.player == player).collect(Collectors.toList());
            if(!reloadPlayer.isEmpty()){
                ReloadPlayer tmpPlayer = reloadPlayer.get(0);
                if(!tmpPlayer.isReloading){
                    double reloadingTimeConfig = Double.parseDouble(BiathlonFlade.plugin.getConfig().get("reloadingTime").toString());
                    long reloadingTime = (long) (reloadingTimeConfig * 20);
                    Bukkit.getScheduler().runTaskLater(BiathlonFlade.plugin, new Runnable() {
                        @Override
                        public void run() {
                            tmpPlayer.isReloading = false;
                            tmpPlayer.isReloaded = true;
                            int index = reloadingPlayers.indexOf(tmpPlayer);
                            reloadingPlayers.set(index, tmpPlayer);
                        }
                    }, reloadingTime);
                }
            }



        }
    }

    public static void removeArrow(Player player) {
        ItemStack[] inventoryContents = player.getInventory().getContents();

        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack item = inventoryContents[i];
            if (item != null && item.getType() == Material.ARROW) {
                // Якщо знайдено стрілу, видаляємо одну одиницю
                item.setAmount(item.getAmount() - 1);

                // Якщо кількість стріл у стопці стала нульовою, видаляємо цей предмет з інвентаря
                if (item.getAmount() <= 0) {
                    inventoryContents[i] = null;
                }

                // Оновлюємо інвентар гравця
                player.getInventory().setContents(inventoryContents);
                player.updateInventory();
                break;
            }
        }
    }
}
