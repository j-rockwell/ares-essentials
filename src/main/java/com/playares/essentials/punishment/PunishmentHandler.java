package com.playares.essentials.punishment;

import com.playares.commons.logger.Logger;
import com.playares.commons.promise.SimplePromise;
import com.playares.commons.services.account.AccountService;
import com.playares.commons.services.alts.AltWatcherService;
import com.playares.commons.services.alts.data.AccountSession;
import com.playares.commons.services.alts.data.AccountSessionDAO;
import com.playares.commons.util.bukkit.Scheduler;
import com.playares.commons.util.general.Time;
import com.playares.essentials.punishment.data.Punishment;
import com.playares.essentials.punishment.data.PunishmentType;
import com.playares.essentials.punishment.menu.LookupMenu;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class PunishmentHandler {
    @Getter public final PunishmentManager manager;
    @Getter public final AccountService accountService;

    public PunishmentHandler(PunishmentManager manager) {
        this.manager = manager;
        this.accountService = (AccountService)manager.getEssentials().getOwner().getService(AccountService.class);
    }

    /**
     * Handles creating a new temp mute
     * @param sender Command Sender
     * @param username Muted Username
     * @param durationName Mute Duration
     * @param reason Mute Reason
     * @param promise Promise
     */
    public void tempmute(CommandSender sender, String username, String durationName, String reason, SimplePromise promise) {
        final UUID creatorId = (sender instanceof Player) ? ((Player)sender).getUniqueId() : null;
        final long duration;

        try {
            duration = Time.parseTime(durationName);
        } catch (NumberFormatException ex) {
            promise.fail("Invalid duration");
            return;
        }

        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                promise.fail("Player not found");
                return;
            }

            final Punishment punishment = new Punishment(PunishmentType.MUTE, aresAccount.getBukkitId(), creatorId, aresAccount.getAddress(), reason, (Time.now() + duration));
            final Player muted = Bukkit.getPlayer(aresAccount.getBukkitId());

            muted.sendMessage(manager.getMuteMessage(punishment));
            manager.setPunishment(false, punishment);

            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                    staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " muted " + aresAccount.getUsername() + " for " + Time.convertToRemaining(punishment.getExpireDate() - Time.now())));

            promise.success();
        });
    }

    /**
     * Handles creating a new mute
     * @param sender Command Sender
     * @param username Muted Username
     * @param reason Mute Reason
     * @param promise Promise
     */
    public void mute(CommandSender sender, String username, String reason, SimplePromise promise) {
        final UUID creatorId = (sender instanceof Player) ? ((Player)sender).getUniqueId() : null;

        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                promise.fail("Player not found");
                return;
            }

            final Punishment punishment = new Punishment(PunishmentType.MUTE, aresAccount.getBukkitId(), creatorId, aresAccount.getAddress(), reason);
            final Player muted = Bukkit.getPlayer(aresAccount.getBukkitId());

            muted.sendMessage(manager.getMuteMessage(punishment));
            manager.setPunishment(false, punishment);

            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                    staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " muted " + aresAccount.getUsername()));

            promise.success();
        });
    }

    /**
     * Handles kicking a player from the server
     * @param sender Command Sender
     * @param username Kicked username
     * @param description Kick reason
     * @param promise Promise
     */
    public void kick(CommandSender sender, String username, String description, SimplePromise promise) {
        final Player kicked = Bukkit.getPlayer(username);

        if (kicked == null || !kicked.isOnline()) {
            promise.fail("Player not found");
            return;
        }

        if (description == null) {
            description = "Reason not given";
        }

        kicked.kickPlayer(ChatColor.RED + description);

        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " kicked " + kicked.getName()));

        Logger.print(sender.getName() + " kicked " + kicked.getName());

        promise.success();
    }

    /**
     * Handles unmuting a player
     * @param sender Command Sender
     * @param username Unmuted Username
     * @param promise Promise
     */
    public void unmute(CommandSender sender, String username, SimplePromise promise) {
        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                promise.fail("Player not found");
                return;
            }

            new Scheduler(manager.getEssentials().getOwner()).async(() -> {

                final Collection<Punishment> activeMutes = manager.getActivePunishments(aresAccount.getUniqueId(), aresAccount.getAddress(), PunishmentType.MUTE);

                new Scheduler(manager.getEssentials().getOwner()).sync(() -> {
                    if (activeMutes.isEmpty()) {
                        promise.fail("Player is not muted");
                        return;
                    }

                    new Scheduler(manager.getEssentials().getOwner()).async(() -> activeMutes.forEach(activeMute -> {
                        activeMute.setAppealed(true);
                        manager.setPunishment(false, activeMute);
                    })).run();

                    Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                            staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " unmuted " + aresAccount.getUsername()));

                    Logger.print(sender.getName() + " unmuted " + aresAccount.getUsername() + " (" + aresAccount.getUniqueId().toString() + ")");
                    promise.success();
                }).run();

            }).run();
        });
    }

    /**
     * Handles unbanning a player
     * @param sender Command Sender
     * @param username Unbanned Username
     * @param promise Promise
     */
    public void unban(CommandSender sender, String username, SimplePromise promise) {
        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                promise.fail("Player not found");
                return;
            }

            new Scheduler(manager.getEssentials().getOwner()).async(() -> {

                final Collection<Punishment> activeBans = manager.getActivePunishments(aresAccount.getUniqueId(), aresAccount.getAddress(), PunishmentType.BAN);

                new Scheduler(manager.getEssentials().getOwner()).sync(() -> {
                    if (activeBans.isEmpty()) {
                        promise.fail("Player is not banned");
                        return;
                    }

                    new Scheduler(manager.getEssentials().getOwner()).async(() -> activeBans.forEach(activeBan -> {
                        activeBan.setAppealed(true);
                        manager.setPunishment(false, activeBan);
                    })).run();

                    Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                            staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " unbanned " + aresAccount.getUsername()));

                    Logger.print(sender.getName() + " unbanned " + aresAccount.getUsername() + " (" + aresAccount.getUniqueId().toString() + ")");
                    promise.success();
                }).run();

            }).run();
        });
    }

    /**
     * Handles unblacklisting a player
     * @param sender Command Sender
     * @param username Unblacklisted Username
     * @param promise Promise
     */
    public void unblacklist(CommandSender sender, String username, SimplePromise promise) {
        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                promise.fail("Player not found");
                return;
            }

            new Scheduler(manager.getEssentials().getOwner()).async(() -> {

                final Collection<Punishment> activeBlacklists = manager.getActivePunishments(aresAccount.getUniqueId(), aresAccount.getAddress(), PunishmentType.BLACKLIST);

                new Scheduler(manager.getEssentials().getOwner()).sync(() -> {
                    if (activeBlacklists.isEmpty()) {
                        promise.fail("Player is not banned");
                        return;
                    }

                    new Scheduler(manager.getEssentials().getOwner()).async(() -> activeBlacklists.forEach(activeBlacklist -> {
                        activeBlacklist.setAppealed(true);
                        manager.setPunishment(false, activeBlacklist);
                    })).run();

                    Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                            staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " unblacklisted " + aresAccount.getUsername()));

                    Logger.print(sender.getName() + " unblacklisted " + aresAccount.getUsername() + " (" + aresAccount.getUniqueId().toString() + ")");
                    promise.success();
                }).run();

            }).run();
        });
    }

    /**
     * Handles creating a new ban
     * @param sender Command Sender
     * @param username Banned Username
     * @param reason Ban Reason
     * @param promise Promise
     */
    public void createBan(CommandSender sender, String username, String reason, SimplePromise promise) {
        final UUID creatorId = (sender instanceof Player) ? ((Player)sender).getUniqueId() : null;

        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                promise.fail("Player not found");
                return;
            }

            final Punishment punishment = new Punishment(PunishmentType.BAN, aresAccount.getBukkitId(), creatorId, aresAccount.getAddress(), reason);
            final Player kicked = Bukkit.getPlayer(aresAccount.getBukkitId());
            final List<Player> matched = manager.getMatchingPlayers(kicked);

            if (!matched.isEmpty()) {
                matched.forEach(matchedPlayer -> kicked.kickPlayer(manager.getKickMessage(punishment)));
            }

            if (kicked != null) {
                kicked.kickPlayer(manager.getKickMessage(punishment));
            }

            manager.setPunishment(false, punishment);

            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                    staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " banned " + aresAccount.getUsername()));

            promise.success();
        });
    }

    /**
     * Handles creating a new temp ban
     * @param sender Command Sender
     * @param username Banned Username
     * @param durationName Ban Duration
     * @param reason Ban Reason
     * @param promise Promise
     */
    public void createTempBan(CommandSender sender, String username, String durationName, String reason, SimplePromise promise) {
        final UUID creatorId = (sender instanceof Player) ? ((Player)sender).getUniqueId() : null;
        final long duration;

        try {
            duration = Time.parseTime(durationName);
        } catch (NumberFormatException ex) {
            promise.fail("Invalid duration");
            return;
        }


        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                promise.fail("Player not found");
                return;
            }

            final Punishment punishment = new Punishment(PunishmentType.BAN, aresAccount.getBukkitId(), creatorId, aresAccount.getAddress(), reason, (Time.now() + duration));
            final Player kicked = Bukkit.getPlayer(aresAccount.getBukkitId());
            final List<Player> matched = manager.getMatchingPlayers(kicked);

            if (!matched.isEmpty()) {
                matched.forEach(matchedPlayer -> kicked.kickPlayer(manager.getKickMessage(punishment)));
            }

            if (kicked != null) {
                kicked.kickPlayer(manager.getKickMessage(punishment));
            }

            manager.setPunishment(false, punishment);

            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                    staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " banned " + aresAccount.getUsername() + " for " + Time.convertToRemaining(punishment.getExpireDate() - Time.now())));

            promise.success();
        });
    }

    /**
     * Handles creating a new blacklist
     * @param sender Command Sender
     * @param username Blacklisted Username
     * @param reason Blacklist Reason
     * @param promise Promise
     */
    public void createBlacklist(CommandSender sender, String username, String reason, SimplePromise promise) {
        final UUID creatorId = (sender instanceof Player) ? ((Player)sender).getUniqueId() : null;

        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                promise.fail("Player not found");
                return;
            }

            final Punishment punishment = new Punishment(PunishmentType.BLACKLIST, aresAccount.getBukkitId(), creatorId, aresAccount.getAddress(), reason);
            final Player kicked = Bukkit.getPlayer(aresAccount.getBukkitId());
            final List<Player> matched = manager.getMatchingPlayers(kicked);

            if (!matched.isEmpty()) {
                matched.forEach(matchedPlayer -> kicked.kickPlayer(manager.getKickMessage(punishment)));
            }

            if (kicked != null) {
                kicked.kickPlayer(manager.getKickMessage(punishment));
            }

            manager.setPunishment(false, punishment);

            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                    staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " blacklisted " + aresAccount.getUsername()));

            promise.success();
        });
    }

    /**
     * Handles performing an account lookup for a player
     * @param player Viewer
     * @param username Viewed Username
     * @param promise Promise
     */
    public void lookup(Player player, String username, SimplePromise promise) {
        final AccountService accountService = (AccountService)manager.getEssentials().getOwner().getService(AccountService.class);
        final AltWatcherService altWatcherService = (AltWatcherService)manager.getEssentials().getOwner().getService(AltWatcherService.class);

        if (accountService == null) {
            promise.fail("Account service not found");
            return;
        }

        if (altWatcherService == null) {
            promise.fail("Alt lookup service not found");
            return;
        }

        accountService.getAccountByUsername(username, aresAccount -> {
            if (aresAccount == null) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            new Scheduler(manager.getEssentials().getOwner()).async(() -> {

                final Collection<Punishment> punishments = manager.getActivePunishments(aresAccount.getUniqueId(), aresAccount.getAddress());
                final Collection<AccountSession> alts = AccountSessionDAO.getSessions(altWatcherService, aresAccount.getBukkitId(), aresAccount.getAddress());

                new Scheduler(manager.getEssentials().getOwner()).sync(() -> {

                    final LookupMenu menu = new LookupMenu(manager.getEssentials().getOwner(), player, aresAccount, punishments, alts);
                    menu.open();
                    promise.success();

                }).run();

            }).run();
        });
    }
}