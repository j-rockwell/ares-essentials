package com.playares.essentials.punishment.listener;

import com.playares.commons.services.alts.event.AltDetectEvent;
import com.playares.essentials.punishment.PunishmentManager;
import com.playares.essentials.punishment.data.Punishment;
import com.playares.essentials.punishment.data.PunishmentType;
import com.playares.commons.event.ProcessedChatEvent;
import com.playares.commons.util.general.IPS;
import com.playares.essentials.staff.data.StaffAccount;
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
    public void onAltDetected(AltDetectEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        manager.getEssentials().getStaffManager().getAccountByPermission(StaffAccount.StaffSetting.SHOW_ALT_NOTIFICATIONS, true).forEach(staffAccount -> {
            final Player staff = Bukkit.getPlayer(staffAccount.getUniqueId());

            if (staffAccount.isVerified()) {
                staff.sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Alt" + ChatColor.GRAY + "] " + ChatColor.RED + player.getName() + ChatColor.GRAY + " has " + ChatColor.BLUE + event.getSessions() + ChatColor.GRAY + " sessions tied to their account");
                staff.sendMessage(ChatColor.GRAY + "Type " + ChatColor.RED + "/lookup " + player.getName() + ChatColor.GRAY + " to further investigate");
            }
        });
    }

    @EventHandler
    public void onLoginAttempt(AsyncPlayerPreLoginEvent event) {
        final UUID uniqueId = event.getUniqueId();
        final long address = IPS.toLong(event.getAddress().getHostAddress());
        final Collection<Punishment> activePunishments = manager.getActivePunishments(uniqueId, address);

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