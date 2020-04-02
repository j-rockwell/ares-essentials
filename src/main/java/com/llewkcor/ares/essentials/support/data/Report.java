package com.llewkcor.ares.essentials.support.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public final class Report implements ISupport {
    @Getter public final UUID creatorUniqueId;
    @Getter public final String creatorUsername;
    @Getter public final long createTime;
    @Getter public final String description;
    @Getter public final UUID reportedUniqueId;
    @Getter public final String reportedUsername;
}
