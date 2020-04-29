package com.playares.essentials.kits;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.playares.commons.logger.Logger;
import com.playares.commons.promise.SimplePromise;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.kits.data.Kit;
import com.playares.commons.util.general.Configs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public final class KitHandler {
    @Getter public final KitManager manager;

    /**
     * Handles showing a list of all kits
     * @param player Player
     */
    public void list(Player player) {
        final List<String> kitNames = Lists.newArrayList();

        manager.getKitRepository().forEach(kit -> kitNames.add(kit.getName()));

        if (kitNames.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No kits found");
            return;
        }

        player.sendMessage(EssentialsService.PRIMARY + "Kits (" + EssentialsService.SECONDARY + kitNames.size() + EssentialsService.PRIMARY + ")");
        player.sendMessage(Joiner.on(", ").join(kitNames));
    }

    /**
     * Handles loading all kits from kits.yml to memory
     */
    @SuppressWarnings("unchecked")
    public void load() {
        if (!manager.getKitRepository().isEmpty()) {
            manager.getKitRepository().clear();
        }

        final YamlConfiguration config = Configs.getConfig(manager.getEssentials().getOwner(), "kits");

        if (config.get("kits") == null) {
            Logger.warn("Skipping loading kits.yml... File is empty");
            return;
        }

        for (String kitName : config.getConfigurationSection("kits").getKeys(false)) {
            final List<ItemStack> contents = (List<ItemStack>)config.getList("kits." + kitName + ".contents");
            final List<ItemStack> armor = (List<ItemStack>)config.getList("kits." + kitName + ".armor");

            final Kit kit = new Kit(kitName, contents, armor);
            manager.getKitRepository().add(kit);
        }

        Logger.print("Loaded " + manager.getKitRepository().size() + " Kits");
    }

    /**
     * Handles giving a player a kit
     * @param player Player
     * @param kitName Kit Name
     * @param promise Promise
     */
    public void give(Player player, String kitName, SimplePromise promise) {
        final Kit kit = manager.getKit(kitName);

        if (kit == null) {
            promise.fail("Kit not found");
            return;
        }

        kit.give(player);
        Logger.print(player.getName() + " gave themself kit " + kit.getName());
    }

    /**
     * Handles giving all nearby players a specified kit
     * @param player Player
     * @param kitName Kit Name
     * @param promise Promise
     */
    public void giveNear(Player player, String kitName, SimplePromise promise) {
        final Kit kit = manager.getKit(kitName);

        if (kit == null) {
            promise.fail("Kit not found");
            return;
        }

        int count = 0;

        for (Entity nearbyEntity : player.getNearbyEntities(32.0, 32.0, 32.0)) {
            if (!(nearbyEntity instanceof Player)) {
                continue;
            }

            final Player otherPlayer = (Player)nearbyEntity;
            kit.give(otherPlayer);

            count += 1;
        }

        player.sendMessage(EssentialsService.PRIMARY + "Applied " + EssentialsService.SECONDARY + kit.getName() + EssentialsService.PRIMARY + " to " + EssentialsService.SPECIAL + count + EssentialsService.PRIMARY + " players");
        Logger.print(player.getName() + " gave kit " + kit.getName() + " to " + count + " players");
        promise.success();
    }

    /**
     * Handles saving a kit to file and process it in memory
     * @param player Player
     * @param kitName Kit Name
     * @param promise Promise
     */
    public void save(Player player, String kitName, SimplePromise promise) {
        final Kit existing = manager.getKit(kitName);

        if (existing != null) {
            promise.fail("Kit name is already in use");
            return;
        }

        final Kit kit = new Kit(kitName, Lists.newArrayList(player.getInventory().getContents()), Lists.newArrayList(player.getInventory().getArmorContents()));
        manager.getKitRepository().add(kit);

        final YamlConfiguration config = Configs.getConfig(manager.getEssentials().getOwner(), "kits");
        config.set("kits." + kitName + ".contents", kit.getContents());
        config.set("kits." + kitName + ".armor", kit.getArmor());
        Configs.saveConfig(manager.getEssentials().getOwner(), "kits", config);

        Logger.print("Saved " + kitName + " to kits.yml");

        promise.success();
    }

    /**
     * Handles deleting a kit from memory and file
     * @param kitName Kit Name
     * @param promise Promise
     */
    public void delete(String kitName, SimplePromise promise) {
        final Kit kit = manager.getKit(kitName);

        if (kit == null) {
            promise.fail("Kit not found");
            return;
        }

        final YamlConfiguration config = Configs.getConfig(manager.getEssentials().getOwner(), "kits");
        config.set("kits." + kit.getName(), null);
        Configs.saveConfig(manager.getEssentials().getOwner(), "kits", config);

        Logger.print("Deleted " + kit.getName() + " from kits.yml");
    }
}
