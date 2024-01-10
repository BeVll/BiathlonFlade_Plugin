package org.bevl.biathlon.biathlonflade;

import org.bevl.biathlon.biathlonflade.models.ReloadPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShootingSystem {
    public static List<ReloadPlayer> reloadingPlayers = new ArrayList<>();

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
                arrow.setDamage(0);
                arrow.setFallDistance(10000);
                arrow.setGravity(false);
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
                arrow.setVelocity(player.getLocation().getDirection().multiply(4.5D));
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
