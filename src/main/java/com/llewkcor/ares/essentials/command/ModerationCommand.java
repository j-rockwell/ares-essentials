package com.llewkcor.ares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.llewkcor.ares.essentials.Essentials;
import com.llewkcor.ares.essentials.menu.InventoryMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class ModerationCommand extends BaseCommand {
    @Getter public final Essentials plugin;

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

        final InventoryMenu menu = new InventoryMenu(plugin, player, observed);
        menu.open();
    }
}