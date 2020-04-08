package com.llewkcor.ares.essentials.reboot;

import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.commons.util.general.Configs;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.essentials.Essentials;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

public final class RebootManager {
    static final String PREFIX = ChatColor.DARK_RED + "[" + ChatColor.RED + "Rebooting" + ChatColor.DARK_RED + "]" + " " + ChatColor.RESET;

    @Getter public final Essentials plugin;
    @Getter public final RebootHandler handler;

    @Getter @Setter public int defaultRebootTime;
    @Getter @Setter public long rebootCommenceTime;
    @Getter @Setter public long rebootTime;
    @Getter @Setter public boolean rebootInProgress;
    @Getter @Setter public BukkitTask ping;

    public RebootManager(Essentials plugin) {
        final YamlConfiguration config = Configs.getConfig(plugin, "config");

        this.plugin = plugin;
        this.handler = new RebootHandler(this);
        this.defaultRebootTime = config.getInt("server_restart.default_reboot_time");
        this.rebootCommenceTime = (Time.now() + (config.getInt("server-restart.server_lifespan") * 1000L));
        this.rebootTime = 0L;
        this.rebootInProgress = false;
        this.ping = new Scheduler(plugin).async(() -> {
            if (!isRebootInProgress() && Time.now() >= getRebootCommenceTime()) {
                handler.startReboot(defaultRebootTime);
            }

            if (isRebootInProgress() && Time.now() >= getRebootTime()) {
                new Scheduler(plugin).sync(() -> {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.kickPlayer(ChatColor.AQUA + "Server is restarting");
                    });

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                }).run();
            }
        }).repeat(20L, 20L).run();
    }

    /**
     * Returns the time in milliseconds before a server reboot starts
     * @return Time in millis
     */
    public long getTimeUntilReboot() {
        if (isRebootInProgress()) {
            return rebootTime - Time.now();
        }

        return rebootCommenceTime - Time.now();
    }
}