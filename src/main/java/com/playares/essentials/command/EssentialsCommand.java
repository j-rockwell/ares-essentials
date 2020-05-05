package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@CommandAlias("essentials|essential")
public final class EssentialsCommand extends BaseCommand {
    @Getter public final EssentialsService service;

    @Subcommand("reload")
    @Description("Reload Essentials")
    @CommandPermission("essentials.reload")
    public void onReload(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Reloading warps...");
        service.getWarpManager().getHandler().load();

        sender.sendMessage(ChatColor.GRAY + "Reloading votes...");
        service.getVoteManager().getHandler().load();

        sender.sendMessage(ChatColor.GRAY + "Reloading kits...");
        service.getKitManager().getHandler().load();

        sender.sendMessage(ChatColor.GRAY + "Reloading broadcasts...");
        service.getBroadcastManager().getHandler().load();

        sender.sendMessage(ChatColor.GREEN + "Finished reloading Essentials");
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}