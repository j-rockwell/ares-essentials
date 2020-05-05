package com.playares.essentials.broadcast;

import com.playares.commons.logger.Logger;
import com.playares.commons.services.account.AccountService;
import com.playares.commons.services.account.data.AresAccount;
import com.playares.commons.util.bukkit.Scheduler;
import com.playares.commons.util.general.Configs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@AllArgsConstructor
public final class BroadcastHandler {
    @Getter public final BroadcastManager manager;

    /**
     * Handles loading all broadcast configuation from file
     */
    public void load() {
        if (!manager.getMessages().isEmpty()) {
            manager.getMessages().clear();
        }

        if (!manager.getQueue().isEmpty()) {
            manager.getQueue().clear();
        }

        if (manager.getTask() != null) {
            manager.getTask().cancel();
            manager.setTask(null);
        }

        final YamlConfiguration config = Configs.getConfig(manager.getEssentials().getOwner(), "broadcasts");

        manager.setInterval(config.getInt("settings.interval"));
        manager.setPrefix((config.get("settings.prefix") != null) ? ChatColor.translateAlternateColorCodes('&', config.getString("settings.prefix")) : "Tip: ");

        for (String message : config.getStringList("messages")) {
            manager.getMessages().add(ChatColor.translateAlternateColorCodes('&', message));
        }

        Logger.print("Loaded " + manager.getMessages().size() + " Broadcast Messages");

        final BukkitTask task = new Scheduler(manager.getEssentials().getOwner()).sync(() -> {
            final AccountService service = (AccountService)manager.getEssentials().getOwner().getService(AccountService.class);

            if (service == null) {
                Logger.error("Account Service could not be found while trying to send a broadcast");
                return;
            }

            final String message = manager.pullMessage();

            for (Player player : Bukkit.getOnlinePlayers()) {
                final AresAccount account = service.getAccountByBukkitID(player.getUniqueId());

                if (account == null || !account.getSettings().isBroadcastsEnabled()) {
                    continue;
                }

                player.sendMessage(manager.getPrefix() + message);
            }

        }).repeat(manager.getInterval() * 20L, manager.getInterval() * 20L).run();

        manager.setTask(task);
    }
}
