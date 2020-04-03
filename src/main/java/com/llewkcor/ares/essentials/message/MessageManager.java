package com.llewkcor.ares.essentials.message;

import com.google.common.collect.Maps;
import com.llewkcor.ares.essentials.Essentials;
import com.llewkcor.ares.essentials.message.listener.MessageListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class MessageManager {
    @Getter public final Essentials plugin;
    @Getter public final MessageHandler handler;
    @Getter public final Map<UUID, UUID> recentMessages;

    public MessageManager(Essentials plugin) {
        this.plugin = plugin;
        this.handler = new MessageHandler(this);
        this.recentMessages = Maps.newConcurrentMap();

        Bukkit.getPluginManager().registerEvents(new MessageListener(this), plugin);
    }

    /**
     * Returns a UUID for the most recently received message for the provided Bukkit Player
     * @param player Bukkit Player
     * @return UUID
     */
    public UUID getRecentlyMessaged(Player player) {
        return getRecentMessages().get(player.getUniqueId());
    }

    /**
     * Sets the most recently messaged player for the provided receiver
     * @param sender Sender
     * @param receiver Receiver
     */
    public void setRecentlyMessaged(Player sender, Player receiver) {
        recentMessages.put(receiver.getUniqueId(), sender.getUniqueId());
    }
}