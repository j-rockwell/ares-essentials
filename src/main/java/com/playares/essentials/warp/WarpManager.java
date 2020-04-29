package com.playares.essentials.warp;

import com.google.common.collect.Sets;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.warp.data.Warp;
import com.playares.essentials.warp.listener.WarpSignListener;
import lombok.Getter;

import java.util.Set;

public final class WarpManager {
    @Getter public final EssentialsService essentials;
    @Getter public final WarpHandler handler;
    @Getter public final Set<Warp> warps;

    public WarpManager(EssentialsService essentials) {
        this.essentials = essentials;
        this.handler = new WarpHandler(this);
        this.warps = Sets.newHashSet();

        essentials.getOwner().registerListener(new WarpSignListener(this));
    }

    /**
     * Returns a warp matching the provided name
     * @param name Warp Name
     * @return Warp
     */
    public Warp getWarp(String name) {
        return warps.stream().filter(warp -> warp.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}