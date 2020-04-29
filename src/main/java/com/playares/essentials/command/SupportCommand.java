package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.playares.commons.promise.SimplePromise;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class SupportCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @CommandAlias("report")
    @Description("Report a player to the staff")
    @Syntax("<username> <reason>")
    public void onReport(Player player, String username, String reason) {
        final Player reported = Bukkit.getPlayer(username);

        plugin.getSupportManager().getHandler().createReport(player, reported, reason, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(ChatColor.GREEN + "Our team has been notified");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("request|helpop")
    @Description("Request a response from our team")
    @Syntax("<description>")
    public void onRequest(Player player, String reason) {
        plugin.getSupportManager().getHandler().createRequest(player, reason, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(ChatColor.GREEN + "Our team has been notified");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("reports")
    @Description("View all open reports")
    @CommandPermission("essentials.report.view")
    public void onViewReports(Player player) {
        plugin.getSupportManager().getHandler().openTicketMenu(player, plugin.getSupportManager().getReports(), new SimplePromise() {
            @Override
            public void success() {}

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("requests")
    @Description("View all open requests")
    @CommandPermission("essentials.request.view")
    public void onViewRequest(Player player) {
        plugin.getSupportManager().getHandler().openTicketMenu(player, plugin.getSupportManager().getRequests(), new SimplePromise() {
            @Override
            public void success() {}

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}
