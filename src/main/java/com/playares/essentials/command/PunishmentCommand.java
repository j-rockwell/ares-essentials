package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.playares.commons.promise.SimplePromise;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class PunishmentCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @CommandAlias("unban")
    @Description("Unban a player")
    @CommandPermission("essentials.punishment.ban")
    @Syntax("<username>")
    public void onUnban(CommandSender sender, String username) {
        plugin.getPunishmentManager().getHandler().unban(sender, username, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Player has been unbanned");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("unblacklist")
    @Description("Unblacklist a player")
    @CommandPermission("essentials.punishment.blacklist")
    @Syntax("<username>")
    public void onUnblacklist(CommandSender sender, String username) {
        plugin.getPunishmentManager().getHandler().unblacklist(sender, username, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Player has been unblacklisted");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("unmute")
    @Description("Unmute a player")
    @CommandPermission("essentials.punishment.mute")
    @Syntax("<username>")
    public void onUnmute(CommandSender sender, String username) {
        plugin.getPunishmentManager().getHandler().unmute(sender, username, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Player has been unmuted");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("kick")
    @Description("Kick a player from the server")
    @CommandPermission("essentials.punishment.kick")
    @Syntax("<username> [reason]")
    public void onKick(CommandSender sender, String username, @Optional String description) {
        plugin.getPunishmentManager().getHandler().kick(sender, username, description, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Player has been kicked");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("mute")
    @Description("Mute a player")
    @CommandPermission("essentials.punishment.mute")
    @Syntax("<username> <reason>")
    public void onMute(CommandSender sender, String username, String description) {
        plugin.getPunishmentManager().getHandler().mute(sender, username, description, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Player has been muted");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("tempmute|tm")
    @Description("Temporarily mute a player")
    @CommandPermission("essentials.punishment.tempmute")
    @Syntax("<username> <duration> <reason>")
    public void onTempmute(CommandSender sender, String username, String duration, String description) {
        plugin.getPunishmentManager().getHandler().tempmute(sender, username, duration, description, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Player has been temporarily muted");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("ban")
    @Description("Ban a player")
    @CommandPermission("essentials.punishment.ban")
    @Syntax("<username> <reason>")
    public void onBan(CommandSender sender, String username, String description) {
        plugin.getPunishmentManager().getHandler().createBan(sender, username, description, new SimplePromise() {
            @Override
            public void success() {}

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("tempban|tb")
    @Description("Temporarily ban a player")
    @CommandPermission("essentials.punishment.tempban")
    @Syntax("<username> <duration> <description>")
    public void onTempban(CommandSender sender, String username, String durationName, String description) {
        plugin.getPunishmentManager().getHandler().createTempBan(sender, username, durationName, description, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Account successfully suspended");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("blacklist")
    @Description("Blacklist a player")
    @CommandPermission("essentials.punishment.blacklist")
    @Syntax("<username> <description>")
    public void onBlacklist(CommandSender sender, String username, String desctipion) {
        plugin.getPunishmentManager().getHandler().createBlacklist(sender, username, desctipion, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Account successfully blacklisted");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}