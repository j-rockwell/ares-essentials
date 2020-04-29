package com.playares.essentials.vanish;

import com.playares.commons.logger.Logger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class VanishHandler {
    @Getter public final VanishManager manager;

    /**
     * Hides existing players for provided player on join
     * @param player Player
     */
    public void hideExisting(Player player) {
        if (manager.getVanished().isEmpty()) {
            return;
        }

        manager.getVanished().forEach(vanishedId -> {
            final Player vanished = Bukkit.getPlayer(vanishedId);

            if (vanished != null) {
                player.hidePlayer(vanished);
            }
        });
    }

    /**
     * Handles hiding the provided player
     * @param player Bukkit Player
     */
    public void hidePlayer(Player player) {
        if (manager.isVanished(player)) {
            return;
        }

        Bukkit.getOnlinePlayers().stream().filter(online ->
                !online.hasPermission("essentials.vanish") && !online.getUniqueId().equals(player.getUniqueId())).forEach(viewer -> viewer.hidePlayer(player));

        manager.getVanished().add(player.getUniqueId());

        Logger.print(player.getName() + " vanished");
    }

    /**
     * Handles showing the provided player
     * @param player Player
     */
    public void showPlayer(Player player) {
        if (!manager.isVanished(player)) {
            return;
        }

        Bukkit.getOnlinePlayers().stream().filter(online -> !online.canSee(player)).forEach(viewer -> viewer.showPlayer(player));

        manager.getVanished().remove(player.getUniqueId());

        Logger.print(player.getName() + " unvanished");
    }
}