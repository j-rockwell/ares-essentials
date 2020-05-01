package com.playares.essentials.warp.listener;

import com.playares.commons.util.bukkit.Players;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.warp.WarpManager;
import com.playares.essentials.warp.data.Warp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public final class WarpScrollListener implements Listener {
    @Getter public final WarpManager manager;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Action action = event.getAction();

        if (!action.equals(Action.RIGHT_CLICK_AIR) && !action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        final ItemStack hand = player.getItemInHand();

        if (hand == null || !hand.getType().equals(Material.EMPTY_MAP)) {
            return;
        }

        if (!hand.hasItemMeta() || !hand.getItemMeta().hasDisplayName()) {
            return;
        }

        if (!hand.getItemMeta().getDisplayName().equals(ChatColor.RED + "Warp Scroll")) {
            return;
        }

        final List<String> lore = hand.getItemMeta().getLore();

        if (lore.isEmpty()) {
            return;
        }

        event.setCancelled(true);
        player.getInventory().setItemInHand(null);

        final String warpName = lore.get(0);
        final Warp warp = manager.getWarp(warpName);

        if (warp == null) {
            player.sendMessage(ChatColor.RED + "Warp not found");
            return;
        }

        player.teleport(warp.getBukkit());
        player.sendMessage(EssentialsService.PRIMARY + "Warped to " + EssentialsService.SECONDARY + warp.getName());
        Players.playSound(player, Sound.ENDERMAN_TELEPORT);
    }
}