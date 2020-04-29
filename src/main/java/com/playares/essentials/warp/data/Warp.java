package com.playares.essentials.warp.data;

import com.playares.commons.location.PLocatable;
import lombok.Getter;

public final class Warp extends PLocatable {
    @Getter public final String name;

    public Warp(String name, double x, double y, double z, float yaw, float pitch, String worldName) {
        super(worldName, x, y, z, yaw, pitch);
        this.name = name;
    }
}