package com.llewkcor.ares.essentials.menu;

import com.google.common.collect.Lists;
import com.llewkcor.ares.commons.item.ItemBuilder;
import com.llewkcor.ares.commons.menu.ClickableItem;
import com.llewkcor.ares.commons.menu.Menu;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.essentials.Essentials;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public final class InventoryMenu extends Menu {
    @Getter public final Player observed;
    @Getter public Scheduler updateScheduler;
    @Getter public BukkitTask updateTask;

    public InventoryMenu(Plugin plugin, Player player, Player observed) {
        super(plugin, player, observed.getName(), 6);
        this.observed = observed;
        this.updateScheduler = new Scheduler(plugin).sync(this::update).repeat(0L, 10L);
    }

    private void update() {
        clear();

        if (observed.isDead() || !observed.isOnline()) {
            updateTask.cancel();
            this.updateTask = null;
            this.updateScheduler = null;

            player.closeInventory();
            player.sendMessage(ChatColor.RED + observed.getName() + " is no longer available");

            return;
        }

        final List<String> potionEffects = Lists.newArrayList();

        observed.getActivePotionEffects().forEach(effect -> potionEffects.add(
                ChatColor.DARK_AQUA + StringUtils.capitalize(effect.getType().getName().toLowerCase().replace("_", " ")) + ChatColor.GRAY + ": " +
                        ChatColor.WHITE + Time.convertToHHMMSS((effect.getDuration() / 20) * 1000)));

        final ItemStack health = new ItemBuilder()
                .setMaterial(Material.SPECKLED_MELON)
                .setName(ChatColor.RED + "Health")
                .addLore(ChatColor.YELLOW + "" + String.format("%.1f", (observed.getHealth() / 2)) + ChatColor.GOLD + "/" + ChatColor.YELLOW + "10.0")
                .build();

        final ItemStack food = new ItemBuilder()
                .setMaterial(Material.COOKED_BEEF)
                .setName(ChatColor.GOLD + "Food")
                .addLore(ChatColor.YELLOW + "" + (observed.getFoodLevel() / 2) + ChatColor.GOLD + "/" + ChatColor.YELLOW + "10")
                .build();

        final ItemStack potions = new ItemBuilder()
                .setMaterial(Material.GLASS_BOTTLE)
                .setName(ChatColor.AQUA + "Potions")
                .addLore(potionEffects)
                .addFlag(ItemFlag.HIDE_POTION_EFFECTS)
                .build();

        for (int i = 0; i < observed.getInventory().getSize(); i++) {
            final ItemStack item = observed.getInventory().getItem(i);

            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }

            addItem(new ClickableItem(item, i, click -> {
                observed.getInventory().removeItem(item);
                player.getInventory().addItem(item);

                observed.sendMessage(Essentials.SECONDARY + player.getName() + Essentials.PRIMARY + " transferred an item from your inventory to theirs");
                player.sendMessage(Essentials.SECONDARY + observed.getName() + Essentials.PRIMARY + "'s item has been transferred to your inventory");
            }));
        }

        for (int i = 45; i < observed.getInventory().getArmorContents().length; i++) {
            final ItemStack item = observed.getInventory().getArmorContents()[i];

            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }

            addItem(new ClickableItem(item, i, click -> {
                observed.getInventory().removeItem(item);
                player.getInventory().addItem(item);

                observed.sendMessage(Essentials.SECONDARY + player.getName() + Essentials.PRIMARY + " transferred an item from your inventory to theirs");
                player.sendMessage(Essentials.SECONDARY + observed.getName() + Essentials.PRIMARY + "'s item has been transferred to your inventory");
            }));
        }

        addItem(new ClickableItem(health, 51, click -> {}));
        addItem(new ClickableItem(food, 52, click -> {}));
        addItem(new ClickableItem(potions, 53, click -> {}));
    }

    @Override
    public void open() {
        super.open();
        this.updateTask = this.updateScheduler.run();
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        super.onInventoryClose(event);

        updateTask.cancel();

        this.updateScheduler = null;
        this.updateTask = null;
    }
}