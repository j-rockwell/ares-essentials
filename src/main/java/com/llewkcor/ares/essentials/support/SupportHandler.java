package com.llewkcor.ares.essentials.support;

import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.essentials.support.data.ISupport;
import com.llewkcor.ares.essentials.support.data.Report;
import com.llewkcor.ares.essentials.support.data.Request;
import com.llewkcor.ares.essentials.support.menu.TicketMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;

@AllArgsConstructor
public final class SupportHandler {
    @Getter public final SupportManager manager;

    /**
     * Handles creating a new report
     * @param player Reporting player
     * @param reported Reported Player
     * @param description Report Description
     * @param promise Promise
     */
    public void createReport(Player player, Player reported, String description, SimplePromise promise) {
        if (manager.getTicketCooldowns().contains(player.getUniqueId())) {
            promise.fail("Please wait a moment before creating another ticket");
            return;
        }

        if (reported == null) {
            promise.fail("Player not found");
            return;
        }

        final Report report = new Report(player.getUniqueId(), player.getName(), Time.now(), description, reported.getUniqueId(), reported.getName());
        manager.getTickets().add(report);

        Bukkit.getOnlinePlayers().stream().filter(online -> online.hasPermission("essentials.report.view")).forEach(staff -> {
            staff.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.RED + "Report" + ChatColor.DARK_RED + "] " + ChatColor.RED + player.getName() + ChatColor.GRAY + " reported " + ChatColor.RED + reported.getName() + ChatColor.GRAY + " for: " + ChatColor.AQUA + description);
            staff.playSound(staff.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
        });

        manager.getTicketCooldowns().add(player.getUniqueId());

        new Scheduler(manager.getPlugin()).sync(() -> manager.getTicketCooldowns().remove(player.getUniqueId())).delay(30 * 20).run();

        promise.success();
    }

    /**
     * Handles creating a new request
     * @param player Requesting Player
     * @param description Request Description
     * @param promise Promise
     */
    public void createRequest(Player player, String description, SimplePromise promise) {
        if (manager.getTicketCooldowns().contains(player.getUniqueId())) {
            promise.fail("Please wait a moment before creating another ticket");
            return;
        }

        final Request request = new Request(player.getUniqueId(), player.getName(), Time.now(), description);
        manager.getTickets().add(request);

        Bukkit.getOnlinePlayers().stream().filter(online -> online.hasPermission("essentials.request.view")).forEach(staff -> {
            staff.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "Request" + ChatColor.DARK_AQUA + "] " + ChatColor.AQUA + player.getName() + ChatColor.GRAY + " requested: " + ChatColor.YELLOW + description);
            staff.playSound(staff.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
        });

        manager.getTicketCooldowns().add(player.getUniqueId());

        new Scheduler(manager.getPlugin()).sync(() -> manager.getTicketCooldowns().remove(player.getUniqueId())).delay(30 * 20).run();

        promise.success();
    }

    /**
     * Handles opening a new ticket menu
     * @param player Viewer
     * @param tickets Tickets to display
     * @param promise Promise
     */
    public void openTicketMenu(Player player, Collection<ISupport> tickets, SimplePromise promise) {
        if (tickets.isEmpty()) {
            promise.fail("No tickets found");
            return;
        }

        final TicketMenu menu = new TicketMenu(manager.getPlugin(), player, tickets);
        menu.open();
        promise.success();
    }
}