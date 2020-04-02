package com.llewkcor.ares.essentials.support.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public final class Request implements ISupport {
    @Getter public final UUID creatorUniqueId;
    @Getter public final String creatorUsername;
    @Getter public final long createTime;
    @Getter public final String description;
}
