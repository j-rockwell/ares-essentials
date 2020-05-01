package com.playares.essentials.kits.listener;

import com.playares.essentials.kits.KitManager;
import com.playares.essentials.kits.data.Kit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public final class KitScrollListener implements Listener {
    @Getter public final KitManager manager;

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

        if (!hand.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Kit Scroll")) {
            return;
        }

        final List<String> lore = hand.getItemMeta().getLore();

        if (lore.isEmpty()) {
            return;
        }

        event.setCancelled(true);

        final String kitName = lore.get(0);
        final Kit kit = manager.getKit(kitName);

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit not found");
            return;
        }

        kit.give(player);
    }
}