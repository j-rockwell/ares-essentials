package com.llewkcor.ares.essentials.punishment.listener;

import com.llewkcor.ares.commons.event.ProcessedChatEvent;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.commons.util.general.IPS;
import com.llewkcor.ares.core.alts.data.AltEntry;
import com.llewkcor.ares.essentials.punishment.PunishmentManager;
import com.llewkcor.ares.essentials.punishment.data.Punishment;
import com.llewkcor.ares.essentials.punishment.data.PunishmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class PunishmentListener implements Listener {
    @Getter public final PunishmentManager manager;

    @EventHandler
    public void onLoginAttempt(AsyncPlayerPreLoginEvent event) {
        final UUID uniqueId = event.getUniqueId();
        final long address = IPS.toLong(event.getAddress().getHostAddress());
        final Collection<Punishment> activePunishments = manager.getActivePunishments(uniqueId, address);
        final Collection<AltEntry> altEntries = manager.getPlugin().getCore().getAltManager().getAlts(uniqueId, address);

        for (Punishment punishment : activePunishments) {
            if (punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, manager.getKickMessage(punishment));
                return;
            }

            if (punishment.getPunishmentType().equals(PunishmentType.BAN) && punishment.getPunishedId().equals(uniqueId)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, manager.getKickMessage(punishment));
                return;
            }
        }

        if (altEntries.size() > 1) {
            new Scheduler(manager.getPlugin()).sync(() -> Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("eessentials.punishment.lookup")).forEach(staff -> {
                staff.sendMessage(ChatColor.RED + "[Alt Detected] " + ChatColor.DARK_PURPLE + event.getName() + ChatColor.GRAY + " has " + ChatColor.LIGHT_PURPLE + altEntries.size() + ChatColor.GRAY + " connected accounts. Type " + ChatColor.AQUA + "/lookup " + event.getName() + ChatColor.GRAY + " to view");
            })).run();
        }
    }

    @EventHandler
    public void onChat(ProcessedChatEvent event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        final long address = IPS.toLong(player.getAddress().getAddress().getHostAddress());
        final Collection<Punishment> activePunishments = manager.getActivePunishments(uniqueId, address);

        for (Punishment mute : activePunishments.stream().filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.MUTE)).collect(Collectors.toList())) {
            event.setCancelled(true);
            player.sendMessage(manager.getMuteMessage(mute));
            break;
        }
    }
}