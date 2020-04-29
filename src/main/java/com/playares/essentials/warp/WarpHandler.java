package com.playares.essentials.warp;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.warp.data.Warp;
import com.playares.commons.logger.Logger;
import com.playares.commons.promise.SimplePromise;
import com.playares.commons.util.general.Configs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public final class WarpHandler {
    @Getter public final WarpManager manager;

    /**
     * Handles loading all warps from file to memory
     */
    public void load() {
        final YamlConfiguration config = Configs.getConfig(manager.getEssentials().getOwner(), "warps");

        if (config.getConfigurationSection("warps") == null) {
            Logger.warn("No warps found, skipping!");
            return;
        }

        for (String warpName : config.getConfigurationSection("warps").getKeys(false)) {
            final double x = config.getDouble("warps." + warpName + ".x");
            final double y = config.getDouble("warps." + warpName + ".y");
            final double z = config.getDouble("warps." + warpName + ".z");
            final float yaw = (float)config.getDouble("warps." + warpName + ".yaw");
            final float pitch = (float)config.getDouble("warps." + warpName + ".pitch");
            final String worldName = config.getString("warps." + warpName + ".world");

            final Warp warp = new Warp(warpName, x, y, z, yaw, pitch, worldName);
            manager.getWarps().add(warp);
        }

        Logger.print("Loaded " + manager.getWarps().size() + " Warps");
    }

    /**
     * Handles saving the warps file
     */
    public void save() {
        final YamlConfiguration config = Configs.getConfig(manager.getEssentials().getOwner(), "warps");

        for (Warp warp : manager.getWarps()) {
            config.set("warps." + warp.getName() + ".x", warp.getX());
            config.set("warps." + warp.getName() + ".y", warp.getY());
            config.set("warps." + warp.getName() + ".z", warp.getZ());
            config.set("warps." + warp.getName() + ".yaw", warp.getYaw());
            config.set("warps." + warp.getName() + ".pitch", warp.getPitch());
            config.set("warps." + warp.getName() + ".world", warp.getWorldName());
        }

        Configs.saveConfig(manager.getEssentials().getOwner(), "warps", config);
        Logger.print("Saved " + manager.getWarps().size() + " Warps");
    }

    /**
     * Handles creating a warp
     * @param player Player
     * @param name Warp Name
     * @param promise Promise
     */
    public void create(Player player, String name, SimplePromise promise) {
        final Warp existing = manager.getWarp(name);

        if (existing != null) {
            promise.fail("Warp name is already in use");
            return;
        }

        final Warp warp = new Warp(name, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch(), player.getLocation().getWorld().getName());

        manager.getWarps().add(warp);
        save();

        Logger.print(warp.getName() + " has been created");

        promise.success();
    }

    /**
     * Handles deleting a warp
     * @param name Warp name
     * @param promise Promise
     */
    public void delete(String name, SimplePromise promise) {
        final Warp warp = manager.getWarp(name);

        if (warp == null) {
            promise.fail("Warp not found");
            return;
        }

        final YamlConfiguration config = Configs.getConfig(manager.getEssentials().getOwner(), "warps");
        config.set("warps." + warp.getName(), null);
        Configs.saveConfig(manager.getEssentials().getOwner(), "warps", config);
        Logger.print(warp.getName() + " has been deleted");

        promise.success();
    }

    /**
     * Handles printing a list of all available warps
     * @param player Player
     */
    public void list(Player player) {
        final List<String> names = Lists.newArrayList();
        manager.getWarps().forEach(warp -> names.add(warp.getName()));
        player.sendMessage(EssentialsService.PRIMARY + "Available Warps (" + EssentialsService.SECONDARY + manager.getWarps().size() + EssentialsService.PRIMARY + ")");
        player.sendMessage(Joiner.on(", ").join(names));
    }
}