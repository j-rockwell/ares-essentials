package com.llewkcor.ares.essentials.kits;

import com.google.common.collect.Sets;
import com.llewkcor.ares.essentials.Essentials;
import com.llewkcor.ares.essentials.kits.data.Kit;
import com.llewkcor.ares.essentials.kits.listener.KitSignListener;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Set;

public final class KitManager {
    @Getter public final Essentials plugin;
    @Getter public final KitHandler handler;
    @Getter public final Set<Kit> kitRepository;

    public KitManager(Essentials plugin) {
        this.plugin = plugin;
        this.handler = new KitHandler(this);
        this.kitRepository = Sets.newConcurrentHashSet();

        Bukkit.getPluginManager().registerEvents(new KitSignListener(this), plugin);
    }

    /**
     * Returns a kit matching the provided name
     * @param name Kit Name
     * @return Kit
     */
    public Kit getKit(String name) {
        return kitRepository.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}