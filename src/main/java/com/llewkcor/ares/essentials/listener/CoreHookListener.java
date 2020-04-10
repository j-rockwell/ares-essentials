package com.llewkcor.ares.essentials.listener;

import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.core.timers.event.HUDUpdateEvent;
import com.llewkcor.ares.essentials.Essentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public final class CoreHookListener implements Listener {
    @Getter public final Essentials plugin;

    @EventHandler
    public void onHudUpdate(HUDUpdateEvent event) {
        if (plugin.getRebootManager().isRebootInProgress()) {
            event.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Restart" + ChatColor.RED + " " + Time.convertToHHMMSS(plugin.getRebootManager().getTimeUntilReboot()));
        }
    }
}