package com.playares.essentials.kits;

import com.google.common.collect.Sets;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.kits.data.Kit;
import com.playares.essentials.kits.listener.KitSignListener;
import lombok.Getter;

import java.util.Set;

public final class KitManager {
    @Getter public final EssentialsService essentials;
    @Getter public final KitHandler handler;
    @Getter public final Set<Kit> kitRepository;

    public KitManager(EssentialsService essentials) {
        this.essentials = essentials;
        this.handler = new KitHandler(this);
        this.kitRepository = Sets.newConcurrentHashSet();

        essentials.getOwner().registerListener(new KitSignListener(this));
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