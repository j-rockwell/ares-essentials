package com.llewkcor.ares.essentials.kits.listener;

import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.essentials.kits.KitManager;
import com.llewkcor.ares.essentials.kits.data.Kit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

@AllArgsConstructor
public final class KitSignListener implements Listener {
    @Getter public final KitManager manager;

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        final Player player = event.getPlayer();
        final String[] lines = event.getLines();
        final String l1 = lines[0], kitName = lines[1];

        if (!l1.equals("kitsign")) {
            return;
        }

        if (!player.hasPermission("essentials.kit")) {
            return;
        }

        final Kit kit = manager.getKit(kitName);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit not found");
            return;
        }

        event.setLine(0, ChatColor.BLUE + "[Load Kit]");
        event.setLine(1, kit.getName());
        event.setLine(2, "");
        event.setLine(3, "");

        Logger.print(player.getName() + " created a kit sign for the kit " + kit.getName());
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        final Action action = event.getAction();
        final Block block = event.getClickedBlock();

        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (block == null || !(block.getType().equals(Material.SIGN) || block.getType().equals(Material.WALL_SIGN))) {
            return;
        }

        final Sign sign = (Sign)block.getState();
        final String[] lines = sign.getLines();
        final String l1 = lines[0], kitName = lines[1];

        if (!l1.equals(ChatColor.BLUE + "[Load Kit]")) {
            return;
        }

        final Kit kit = manager.getKit(kitName);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit not found");
            return;
        }

        kit.give(player);
        player.sendMessage(ChatColor.GREEN + "Loaded " + ChatColor.AQUA + kit.getName());
    }
}
