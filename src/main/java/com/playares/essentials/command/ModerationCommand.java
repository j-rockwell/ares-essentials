package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.menu.InventoryMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class ModerationCommand extends BaseCommand {
    @Getter public final EssentialsService essentials;

    @CommandAlias("invsee|inventory|inv")
    @CommandPermission("essentials.invsee")
    @CommandCompletion("@players")
    @Syntax("<username>")
    @Description("View a player's inventory")
    public void onInventorySee(Player player, String username) {
        final Player observed = Bukkit.getPlayer(username);

        if (observed == null || !observed.isOnline() || observed.isDead()) {
            player.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        final InventoryMenu menu = new InventoryMenu(essentials.getOwner(), player, observed);
        menu.open();
    }

    @CommandAlias("vanish|v|hide")
    @CommandPermission("essentials.vanish")
    @Description("Hide from others")
    public void onVanish(Player player) {
        if (essentials.getVanishManager().isVanished(player)) {
            player.sendMessage(ChatColor.RED + "You are already vanished");
            return;
        }

        essentials.getVanishManager().getHandler().hidePlayer(player);
        player.sendMessage(ChatColor.DARK_AQUA + "You are now hidden from others");
    }

    @CommandAlias("unvanish|show|uv")
    @CommandPermission("essentials.vanish")
    @Description("Show yourself from being previously vanished")
    public void onUnvanish(Player player) {
        if (!essentials.getVanishManager().isVanished(player)) {
            player.sendMessage(ChatColor.RED + "You are not vanished");
            return;
        }

        essentials.getVanishManager().getHandler().showPlayer(player);
        player.sendMessage(ChatColor.DARK_AQUA + "You are now visible to others");
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}