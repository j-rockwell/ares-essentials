package com.playares.essentials.message.listener;

import com.playares.essentials.message.MessageManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public final class MessageListener implements Listener {
    @Getter public final MessageManager manager;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        manager.getRecentMessages().remove(player.getUniqueId());
    }
}