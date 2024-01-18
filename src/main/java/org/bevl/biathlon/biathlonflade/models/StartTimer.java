package org.bevl.biathlon.biathlonflade.models;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bevl.biathlon.biathlonflade.BiathlonFlade;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartTimer extends BukkitRunnable {
    private final Location location;
    private int secondsLeft;
    private int times;
    private Player senderPlayer;

    public StartTimer(Location location, Player senderPlayer) {
        this.location = location;
        this.secondsLeft = 5;
        this.times = 0;
        this.senderPlayer = senderPlayer;
    }

    @Override
    public void run() {
        if(times >= BiathlonFlade.race.startList.size()){
            cancel();
        }
        else {
            if(secondsLeft == 5){
                senderPlayer.playSound(location, "minecraft:count", SoundCategory.AMBIENT, 1.0f, 1.0f);

            }
            else if (secondsLeft >= 0) {
                // Отримання всіх гравців у радіусі 5 метрів від місця, де була викликана команда

                if(secondsLeft == 0)
                    times++;
                // Зменшення лічильника секунд

            } else if (secondsLeft < 0) {
                secondsLeft = BiathlonFlade.raceSettings.StartWaitSeconds-1;

            }
        }
        for (Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
            Location playerLocation = nearbyPlayer.getLocation();


            // Перевірка, чи гравець знаходиться в радіусі 5 метрів від місця команди
            if (location.distance(playerLocation) <= 20) {
                nearbyPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cStart in §l" + secondsLeft + " §r§cseconds"));
                if(secondsLeft == 0)
                    nearbyPlayer.sendMessage("§3Next player is §2" + BiathlonFlade.race.startList.get(times));
            }
        }
        secondsLeft--;
    }
}
