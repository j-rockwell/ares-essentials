package com.playares.essentials.vanish;

import com.google.common.collect.Sets;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.vanish.listener.VanishListener;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public final class VanishManager {
    @Getter public final EssentialsService essentials;
    @Getter public final VanishHandler handler;
    @Getter public final Set<UUID> vanished;

    public VanishManager(EssentialsService essentials) {
        this.essentials = essentials;
        this.handler = new VanishHandler(this);
        this.vanished = Sets.newConcurrentHashSet();

        essentials.getOwner().registerListener(new VanishListener(this));
    }

    /**
     * Returns true if the provided Bukkit Player is vanished
     * @param player Bukkit Player
     * @return True if vanished
     */
    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }
}