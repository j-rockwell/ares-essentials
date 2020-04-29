package com.playares.essentials.message;

import com.google.common.collect.Maps;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.message.listener.MessageListener;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class MessageManager {
    @Getter public final EssentialsService essentials;
    @Getter public final MessageHandler handler;
    @Getter public final Map<UUID, UUID> recentMessages;

    public MessageManager(EssentialsService essentials) {
        this.essentials = essentials;
        this.handler = new MessageHandler(this);
        this.recentMessages = Maps.newConcurrentMap();

        essentials.getOwner().registerListener(new MessageListener(this));
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