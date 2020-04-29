package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.playares.commons.logger.Logger;
import com.playares.commons.promise.SimplePromise;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.warp.data.Warp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("warp")
@AllArgsConstructor
public final class WarpCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @CommandAlias("warp")
    @Description("Warp to a location")
    @CommandPermission("essentials.warp")
    @Syntax("<name>")
    public void onWarp(Player player, String name) {
        if (name.equalsIgnoreCase("list")) {
            return;
        }

        final Warp warp = plugin.getWarpManager().getWarp(name);

        if (warp == null) {
            player.sendMessage(ChatColor.RED + "Warp not found");
            return;
        }

        player.teleport(warp.getBukkit());
        player.sendMessage(EssentialsService.PRIMARY + "Warped to " + EssentialsService.SECONDARY + warp.getName());
        Logger.print(player.getName() + " warped to " + warp.getName());
    }

    @Subcommand("create")
    @Description("Create a new warp")
    @CommandPermission("essentials.warp")
    @Syntax("<name>")
    public void onCreate(Player player, String name) {
        plugin.getWarpManager().getHandler().create(player, name, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(EssentialsService.PRIMARY + "Warp has been created");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("delete|del")
    @Description("Delete a warp")
    @CommandPermission("essentials.warp")
    @Syntax("<name>")
    public void onDeleteWarp(Player player, String name) {
        plugin.getWarpManager().getHandler().delete(name, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(EssentialsService.PRIMARY + "Warp has been deleted");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("list")
    @Description("List all warps")
    @CommandPermission("essentials.warp")
    public void onList(Player player) {
        plugin.getWarpManager().getHandler().list(player);
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}