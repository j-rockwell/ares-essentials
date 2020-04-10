package com.llewkcor.ares.essentials.punishment;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Lists;
import com.llewkcor.ares.commons.util.general.IPS;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.essentials.Essentials;
import com.llewkcor.ares.essentials.punishment.data.Punishment;
import com.llewkcor.ares.essentials.punishment.data.PunishmentDAO;
import com.llewkcor.ares.essentials.punishment.data.PunishmentType;
import com.llewkcor.ares.essentials.punishment.listener.PunishmentListener;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PunishmentManager {
    @Getter public final Essentials plugin;
    @Getter public final PunishmentHandler handler;

    public PunishmentManager(Essentials plugin) {
        this.plugin = plugin;
        this.handler = new PunishmentHandler(this);

        Bukkit.getPluginManager().registerEvents(new PunishmentListener(this), plugin);
    }

    /**
     * Returns an Immutable Collection containing all active punishments for the provided Bukkit UUID and IP Address
     * @param uniqueId Bukkit UUID
     * @param address Converted IP Address
     * @return Immutable Collection of Punishments
     */
    public ImmutableCollection<Punishment> getActivePunishments(UUID uniqueId, long address) {
        return PunishmentDAO.getPunishments(plugin.getCore().getDatabaseInstance(),
                Filters.or(Filters.eq("punished_id", uniqueId), Filters.eq("punished_address", address)),
                Filters.and(Filters.eq("appealed", false), Filters.or(Filters.eq("forever", true), Filters.gt("expire_date", Time.now()))));
    }

    /**
     * Returns a kick message for the provided Punishment
     * @param punishment Punishment
     * @return Kick message
     */
    public String getKickMessage(Punishment punishment) {
        final List<String> result = Lists.newArrayList();

        if (punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)) {
            result.add(ChatColor.RED + "Your account has been blacklisted from Ares");
        }

        if (punishment.getPunishmentType().equals(PunishmentType.BAN)) {
            result.add(ChatColor.RED + "Your account has been banned from Ares");

            if (punishment.isForever()) {
                result.add(ChatColor.RED + "This punishment will " + ChatColor.RED + "" + ChatColor.UNDERLINE + "never" + ChatColor.RED + " expire");
            } else {
                result.add(ChatColor.RED + "This punishment will expire in " + Time.convertToRemaining(punishment.getExpireDate() - Time.now()));
            }
        }

        // TODO: Update to website domain
        result.add(ChatColor.RED + "Appeal at https://playares.com/appeal");
        return Joiner.on(ChatColor.RESET + "\n").join(result);
    }

    /**
     * Returns a chat message for the provided mute punishment
     * @param punishment Punishment
     * @return Mute message
     */
    public String getMuteMessage(Punishment punishment) {
        List<String> result = Lists.newArrayList();

        if (punishment.isForever()) {
            result.add(ChatColor.RED + "You have been silenced for: " + punishment.getReason());
            result.add(ChatColor.RED + "This punishment will not expire");
        } else {
            result.add(ChatColor.RED + "You have been temporarily silenced for: " + punishment.getReason());
            result.add(ChatColor.RED + "This punishment will expire in " + Time.convertToRemaining(punishment.getExpireDate() - Time.now()));
        }

        result.add(ChatColor.RED + "Appeal at https://playares.com/appeal");
        return Joiner.on(ChatColor.RESET + "\n").join(result);
    }

    /**
     * Returns a List of players that have the same IP address as the provided player
     * @param player Player
     * @return List of Players
     */
    public List<Player> getMatchingPlayers(Player player) {
        final long address = IPS.toLong(player.getAddress().getAddress().getHostAddress());
        return Bukkit.getOnlinePlayers().stream().filter(otherPlayer -> !otherPlayer.getUniqueId().equals(player.getUniqueId()) &&
                IPS.toLong(otherPlayer.getAddress().getAddress().getHostAddress()) == address).collect(Collectors.toList());
    }
}