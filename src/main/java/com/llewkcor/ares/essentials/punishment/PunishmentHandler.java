package com.llewkcor.ares.essentials.punishment;

import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.commons.promise.FailablePromise;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.core.player.data.account.AresAccount;
import com.llewkcor.ares.essentials.punishment.data.Punishment;
import com.llewkcor.ares.essentials.punishment.data.PunishmentDAO;
import com.llewkcor.ares.essentials.punishment.data.PunishmentType;
import com.llewkcor.ares.essentials.punishment.menu.PlayerLookupMenu;
import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class PunishmentHandler {
    @Getter public final PunishmentManager manager;

    /**
     * Handles opening lookup GUI for provided username
     * @param player Player
     * @param username Username
     * @param promise Promise
     */
    public void lookup(Player player, String username, SimplePromise promise) {
        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    promise.fail("Player not found");
                    return;
                }

                new Scheduler(manager.getPlugin()).async(() -> {
                    final Collection<Punishment> activePunishments = PunishmentDAO.getPunishments(manager.getPlugin().getCore().getDatabaseInstance(),
                            Filters.or(Filters.eq("punished_id", aresAccount.getBukkitId()), Filters.eq("punished_address", aresAccount.getAddress())),
                            Filters.eq("punished_id", aresAccount.getBukkitId()));

                    new Scheduler(manager.getPlugin()).sync(() -> manager.getPlugin().getCore().getAltManager().getAlts(aresAccount.getUniqueId(), aresAccount.getAddress(), altEntries -> {
                        final PlayerLookupMenu menu = new PlayerLookupMenu(manager.getPlugin(), player, aresAccount, activePunishments, altEntries);
                        menu.open();
                    })).run();
                }).run();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
        });
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

        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    promise.fail("Player not found");
                    return;
                }

                final Punishment punishment = new Punishment(PunishmentType.MUTE, aresAccount.getBukkitId(), creatorId, aresAccount.getAddress(), reason, (Time.now() + duration));
                final Player muted = Bukkit.getPlayer(aresAccount.getBukkitId());

                muted.sendMessage(manager.getMuteMessage(punishment));

                new Scheduler(manager.getPlugin()).async(() -> PunishmentDAO.savePunishment(manager.getPlugin().getCore().getDatabaseInstance(), punishment)).run();

                Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                        staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " muted " + aresAccount.getUsername() + " for " + Time.convertToRemaining(punishment.getExpireDate() - Time.now())));

                promise.success();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
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

        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    promise.fail("Player not found");
                    return;
                }

                final Punishment punishment = new Punishment(PunishmentType.MUTE, aresAccount.getBukkitId(), creatorId, aresAccount.getAddress(), reason);
                final Player muted = Bukkit.getPlayer(aresAccount.getBukkitId());

                muted.sendMessage(manager.getMuteMessage(punishment));

                new Scheduler(manager.getPlugin()).async(() -> PunishmentDAO.savePunishment(manager.getPlugin().getCore().getDatabaseInstance(), punishment)).run();

                Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                        staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " muted " + aresAccount.getUsername()));

                promise.success();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
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
        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    promise.fail("Player not found");
                    return;
                }

                new Scheduler(manager.getPlugin()).async(() -> {
                    final Collection<Punishment> activePunishments = manager.getActivePunishments(aresAccount.getUniqueId(), aresAccount.getAddress());
                    final Collection<Punishment> activeMutes = activePunishments.stream().filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.MUTE)).collect(Collectors.toList());

                    new Scheduler(manager.getPlugin()).sync(() -> {
                        if (activeMutes.isEmpty()) {
                            promise.fail("Player is not muted");
                            return;
                        }

                        new Scheduler(manager.getPlugin()).async(() -> {
                            activeMutes.forEach(activeMute -> {
                                activeMute.setAppealed(true);
                                PunishmentDAO.savePunishment(manager.getPlugin().getCore().getDatabaseInstance(), activeMute);
                            });
                        }).run();

                        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                                staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " unmuted " + aresAccount.getUsername()));

                        Logger.print(sender.getName() + " unmuted " + aresAccount.getUsername() + " (" + aresAccount.getUniqueId().toString() + ")");
                        promise.success();
                    }).run();
                }).run();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
        });
    }

    /**
     * Handles unbanning a player
     * @param sender Command Sender
     * @param username Unbanned Username
     * @param promise Promise
     */
    public void unban(CommandSender sender, String username, SimplePromise promise) {
        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    promise.fail("Player not found");
                    return;
                }

                new Scheduler(manager.getPlugin()).async(() -> {
                    final Collection<Punishment> activePunishments = manager.getActivePunishments(aresAccount.getUniqueId(), aresAccount.getAddress());
                    final Collection<Punishment> activeBans = activePunishments.stream().filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.BAN)).collect(Collectors.toList());

                    new Scheduler(manager.getPlugin()).sync(() -> {
                        if (activeBans.isEmpty()) {
                            promise.fail("Player is not banned");
                            return;
                        }

                        new Scheduler(manager.getPlugin()).async(() -> {
                            activeBans.forEach(activeBan -> {
                                activeBan.setAppealed(true);
                                PunishmentDAO.savePunishment(manager.getPlugin().getCore().getDatabaseInstance(), activeBan);
                            });
                        }).run();

                        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                                staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " unbanned " + aresAccount.getUsername()));

                        Logger.print(sender.getName() + " unbanned " + aresAccount.getUsername() + " (" + aresAccount.getUniqueId().toString() + ")");
                        promise.success();
                    }).run();
                }).run();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
        });
    }

    /**
     * Handles unblacklisting a player
     * @param sender Command Sender
     * @param username Unblacklisted Username
     * @param promise Promise
     */
    public void unblacklist(CommandSender sender, String username, SimplePromise promise) {
        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    promise.fail("Player not found");
                    return;
                }

                new Scheduler(manager.getPlugin()).async(() -> {
                    final Collection<Punishment> activePunishments = manager.getActivePunishments(aresAccount.getUniqueId(), aresAccount.getAddress());
                    final Collection<Punishment> activeBans = activePunishments.stream().filter(punishment -> punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)).collect(Collectors.toList());

                    new Scheduler(manager.getPlugin()).sync(() -> {
                        if (activeBans.isEmpty()) {
                            promise.fail("Player is not banned");
                            return;
                        }

                        new Scheduler(manager.getPlugin()).async(() -> {
                            activeBans.forEach(activeBan -> {
                                activeBan.setAppealed(true);
                                PunishmentDAO.savePunishment(manager.getPlugin().getCore().getDatabaseInstance(), activeBan);
                            });
                        }).run();

                        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                                staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " unblacklisted " + aresAccount.getUsername()));

                        Logger.print(sender.getName() + " unblacklisted " + aresAccount.getUsername() + " (" + aresAccount.getUniqueId().toString() + ")");
                        promise.success();
                    }).run();
                }).run();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
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

        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
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

                new Scheduler(manager.getPlugin()).async(() -> PunishmentDAO.savePunishment(manager.getPlugin().getCore().getDatabaseInstance(), punishment)).run();

                Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                        staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " banned " + aresAccount.getUsername()));

                promise.success();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
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

        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
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

                new Scheduler(manager.getPlugin()).async(() -> PunishmentDAO.savePunishment(manager.getPlugin().getCore().getDatabaseInstance(), punishment)).run();

                Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                        staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " banned " + aresAccount.getUsername() + " for " + Time.convertToRemaining(punishment.getExpireDate() - Time.now())));

                promise.success();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
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

        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
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

                new Scheduler(manager.getPlugin()).async(() -> PunishmentDAO.savePunishment(manager.getPlugin().getCore().getDatabaseInstance(), punishment)).run();

                Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("essentials.punishment.view")).forEach(staff ->
                        staff.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + " blacklisted " + aresAccount.getUsername() + " for " + Time.convertToRemaining(punishment.getExpireDate() - Time.now())));

                promise.success();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
        });
    }
}