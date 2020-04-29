package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
public final class ChatCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @CommandAlias("broadcast|b")
    @CommandPermission("essentials.broadcast")
    @Syntax("[-p] <message>")
    @Description("Broadcast a message")
    public void onBroadcast(CommandSender sender, String message) {
        final String[] split = message.split(" ");
        final boolean asPlayer = (split.length > 1 && split[0].equalsIgnoreCase("-p"));

        Bukkit.broadcastMessage(" ");

        if (asPlayer) {
            final String trimmed = message.substring(3);
            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "[" + ChatColor.DARK_RED + sender.getName() + ChatColor.DARK_AQUA + "] " + ChatColor.AQUA + trimmed);
        } else {
            Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "[" + ChatColor.DARK_RED + "Ares" + ChatColor.DARK_AQUA + "] " + ChatColor.AQUA + message);
        }

        Bukkit.broadcastMessage(" ");
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}