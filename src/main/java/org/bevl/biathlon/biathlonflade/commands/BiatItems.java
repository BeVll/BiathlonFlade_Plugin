package org.bevl.biathlon.biathlonflade.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.HashMap;

public class BiatItems implements CommandExecutor {
    public static final HashMap<Player, Inventory> INVENTORIES = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        try {
            if(!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            Inventory copperInventory = Bukkit.createInventory(null , 4 * 9 , "§5Biathlon items:");
            copperInventory.addItem(new ItemStack(Material.BOW, 1));
            copperInventory.addItem(new ItemStack(Material.ARROW, 64));
            copperInventory.addItem(new ItemStack(Material.STICK, 2));
            player.openInventory(copperInventory);
            INVENTORIES.put(player, copperInventory);

        } catch (Exception exception){
            System.out.print(exception.toString());
            Bukkit.broadcastMessage(exception.toString());
        }


        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
