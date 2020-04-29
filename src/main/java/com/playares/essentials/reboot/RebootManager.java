package com.playares.essentials.reboot;

import com.playares.commons.util.bukkit.Scheduler;
import com.playares.commons.util.general.Configs;
import com.playares.commons.util.general.Time;
import com.playares.essentials.EssentialsService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

public final class RebootManager {
    static final String PREFIX = ChatColor.DARK_RED + "[" + ChatColor.RED + "Rebooting" + ChatColor.DARK_RED + "]" + " " + ChatColor.RESET;

    @Getter public final EssentialsService essentials;
    @Getter public final RebootHandler handler;

    @Getter @Setter public int defaultRebootTime;
    @Getter @Setter public long rebootCommenceTime;
    @Getter @Setter public long rebootTime;
    @Getter @Setter public boolean rebootInProgress;
    @Getter @Setter public BukkitTask ping;

    public RebootManager(EssentialsService essentials) {
        final YamlConfiguration config = Configs.getConfig(essentials.getOwner(), "essentials");

        this.essentials = essentials;
        this.handler = new RebootHandler(this);
        this.defaultRebootTime = config.getInt("server_restart.default_reboot_time");
        this.rebootCommenceTime = Time.now() + (config.getInt("server_restart.server_lifespan") * 1000L);
        this.rebootTime = 0L;
        this.rebootInProgress = false;
        this.ping = new Scheduler(essentials.getOwner()).async(() -> {
            if (!isRebootInProgress() && Time.now() >= getRebootCommenceTime()) {
                handler.startReboot(defaultRebootTime);
            }

            if (isRebootInProgress() && Time.now() >= getRebootTime()) {
                new Scheduler(essentials.getOwner()).sync(() -> {
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