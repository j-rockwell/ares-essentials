package com.playares.essentials.vanish.menu;

import com.playares.commons.menu.ClickableItem;
import com.playares.commons.menu.Menu;
import com.playares.commons.util.bukkit.Scheduler;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class ChestMenu extends Menu {
    @Getter public final Inventory inventory;
    @Getter public Scheduler updateScheduler;
    @Getter public BukkitTask updateTask;

    public ChestMenu(Plugin plugin, Player player, Inventory inventory) {
        super(plugin, player, inventory.getName(), inventory.getSize() / 9);
        this.inventory = inventory;
        this.updateScheduler = new Scheduler(plugin).sync(() -> update()).repeat(0L, 10L);
    }

    private void update() {
        clear();

        if (inventory == null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Inventory no longer exists");
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack item = inventory.getItem(i);

            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }

            addItem(new ClickableItem(item, i, click -> {
                inventory.removeItem(item);
                player.getInventory().addItem(item);
            }));
        }
    }

    @Override
    public void open() {
        super.open();
        this.updateTask = updateScheduler.run();
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        super.onInventoryClose(event);
        this.updateTask.cancel();
        this.updateTask = null;
        this.updateScheduler = null;
    }
}
