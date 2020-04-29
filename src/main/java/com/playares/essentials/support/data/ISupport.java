package com.playares.essentials.support.data;

import java.util.UUID;

public interface ISupport {
    /**
     * Returns the UUID of the creator
     * @return UUID
     */
    UUID getCreatorUniqueId();

    /**
     * Returns the username of the creator
     * @return Username
     */
    String getCreatorUsername();

    /**
     * Returns the time in ms this ticket was created
     * @return Time
     */
    long getCreateTime();

    /**
     * Returns the description of this ticket
     * @return Description
     */
    String getDescription();
}
