package com.playares.essentials.broadcast;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.playares.commons.logger.Logger;
import com.playares.essentials.EssentialsService;
import com.playares.commons.services.account.AccountService;
import com.playares.commons.services.account.data.AresAccount;
import com.playares.commons.util.bukkit.Scheduler;
import com.playares.commons.util.general.Configs;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Queue;

public final class BroadcastManager {
    @Getter public final EssentialsService essentials;
    @Getter public final int interval;
    @Getter public final String prefix;
    @Getter public final List<String> messages;
    @Getter public final Queue<String> queue;
    @Getter public final BukkitTask task;

    public BroadcastManager(EssentialsService essentials) {
        final YamlConfiguration config = Configs.getConfig(essentials.getOwner(), "broadcasts");

        this.essentials = essentials;
        this.interval = config.getInt("settings.interval");
        this.prefix = (config.get("settings.prefix") != null) ? ChatColor.translateAlternateColorCodes('&', config.getString("settings.prefix")) : "Tip: ";
        this.messages = Lists.newArrayList();
        this.queue = Queues.newConcurrentLinkedQueue();

        for (String message : config.getStringList("messages")) {
            messages.add(ChatColor.translateAlternateColorCodes('&', message));
        }

        Logger.print("Loaded " + messages.size() + " Broadcast Messages");

        this.task = new Scheduler(essentials.getOwner()).sync(() -> {
            final AccountService service = (AccountService)essentials.getOwner().getService(AccountService.class);

            if (service == null) {
                Logger.error("Account Service could not be found while trying to send a broadcast");
                return;
            }

            final String message = pullMessage();

            for (Player player : Bukkit.getOnlinePlayers()) {
                final AresAccount account = service.getAccountByBukkitID(player.getUniqueId());

                if (account == null || !account.getSettings().isBroadcastsEnabled()) {
                    continue;
                }

                player.sendMessage(prefix + message);
            }

        }).repeat(interval * 20L, interval * 20L).run();
    }

    /**
     * Pulls a new message from the broadcast queue
     * @return Message
     */
    private String pullMessage() {
        if (queue.isEmpty()) {
            if (messages.isEmpty()) {

            }
            queue.addAll(messages);
        }

        return queue.remove();
    }
}
