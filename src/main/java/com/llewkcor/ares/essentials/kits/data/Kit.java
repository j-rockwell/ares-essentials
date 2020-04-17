package com.llewkcor.ares.essentials.kits.data;

import com.llewkcor.ares.commons.util.bukkit.Players;
import com.llewkcor.ares.essentials.Essentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public final class Kit {
    @Getter public final String name;
    @Getter public final List<ItemStack> contents;
    @Getter public final List<ItemStack> armor;

    /**
     * Gives this kit to the provided player
     * @param player Player
     */
    public void give(Player player) {
        Players.resetHealth(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        int contentCursor = 0;
        for (ItemStack item : contents) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                player.getInventory().setItem(contentCursor, item);
            }

            contentCursor += 1;
        }

        // Load armor
        ItemStack[] armorContents = new ItemStack[armor.size()];
        armorContents = armor.toArray(armorContents);
        player.getInventory().setArmorContents(armorContents);

        player.sendMessage(Essentials.PRIMARY + "You have received kit " + Essentials.SECONDARY + name);
    }
}