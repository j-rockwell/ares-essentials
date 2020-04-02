package com.llewkcor.ares.essentials.support;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.llewkcor.ares.essentials.Essentials;
import com.llewkcor.ares.essentials.support.data.ISupport;
import com.llewkcor.ares.essentials.support.data.Report;
import com.llewkcor.ares.essentials.support.data.Request;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SupportManager {
    @Getter public final Essentials plugin;
    @Getter public final SupportHandler handler;
    @Getter public final Set<ISupport> tickets;
    @Getter public final List<UUID> ticketCooldowns;

    public SupportManager(Essentials plugin) {
        this.plugin = plugin;
        this.handler = new SupportHandler(this);
        this.tickets = Sets.newConcurrentHashSet();
        this.ticketCooldowns = Collections.synchronizedList(Lists.newArrayList());
    }

    /**
     * Returns true if the provided player is on a ticket cooldown
     * @param player Bukkit Player
     * @return True if on ticket cooldown
     */
    public boolean isTicketCooldown(Player player) {
        return ticketCooldowns.contains(player.getUniqueId());
    }

    /**
     * Returns an Immutable List containing all open reports
     * @return Immutable List of Reports
     */
    public ImmutableList<ISupport> getReports() {
        return ImmutableList.copyOf(tickets.stream().filter(ticket -> ticket instanceof Report).collect(Collectors.toList()));
    }

    /**
     * Returns an Immutable List containing all open requests
     * @return Immutable List of Requests
     */
    public ImmutableList<ISupport> getRequests() {
        return ImmutableList.copyOf(tickets.stream().filter(ticket -> ticket instanceof Request).collect(Collectors.toList()));
    }

    /**
     * Returns an Immutable List of reports for the provided username
     * @param username Username
     * @return Immutable List of Reports
     */
    public ImmutableList<ISupport> getReportsFor(String username) {
        return ImmutableList.copyOf(getReports().stream().filter(report -> ((Report)report).getReportedUsername().equalsIgnoreCase(username)).collect(Collectors.toList()));
    }
}