package com.llewkcor.ares.essentials.reboot;

import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.commons.util.general.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
public final class RebootHandler {
    @Getter public final RebootManager manager;

    /**
     * Handles starting a reboot for the provided time
     * @param seconds Time in seconds
     */
    public void startReboot(int seconds) {
        manager.setRebootTime(Time.now() + (seconds * 1000L));
        manager.setRebootInProgress(true);

        Bukkit.broadcastMessage(RebootManager.PREFIX + ChatColor.RED + "The server will restart in " + ChatColor.BLUE + Time.convertToRemaining(manager.getTimeUntilReboot()));
    }

    /**
     * Handles starting a reboot countdown
     * @param sender CommandSender
     * @param promise Promise
     */
    public void startReboot(CommandSender sender, SimplePromise promise) {
        startReboot(manager.getDefaultRebootTime());
        Logger.print(sender.getName() + " manually started a reboot");
        promise.success();
    }

    /**
     * Handles starting a reboot countdown
     * @param sender CommandSender
     * @param timeName Time
     * @param promise Promise
     */
    public void startReboot(CommandSender sender, String timeName, SimplePromise promise) {
        final long ms;

        try {
            ms = Time.parseTime(timeName);
        } catch (NumberFormatException ex) {
            promise.fail("Invalid time format");
            return;
        }

        startReboot((int)(ms / 1000L));
        Logger.print(sender.getName() + " manually started a reboot");
        promise.success();
    }

    /**
     * Handles cancelling a reboot
     * @param sender CommandSender
     * @param promise Promise
     */
    public void cancelReboot(CommandSender sender, SimplePromise promise) {
        if (!manager.isRebootInProgress()) {
            promise.fail("There is not reboot in progress");
            return;
        }

        manager.setRebootInProgress(false);
        Bukkit.broadcastMessage(RebootManager.PREFIX + ChatColor.RED + "Reboot has been cancelled");
        Logger.print(sender.getName() + " cancelled the current reboot process");
        promise.success();
    }

    /**
     * Handles scheduling a reboot
     * @param sender CommandSender
     * @param timeName Time
     * @param promise Promise
     */
    public void scheduleReboot(CommandSender sender, String timeName, SimplePromise promise) {
        final long ms;

        try {
            ms = Time.parseTime(timeName);
        } catch (NumberFormatException ex) {
            promise.fail("Invalid time format");
            return;
        }

        if (manager.isRebootInProgress()) {
            manager.setRebootInProgress(false);
            Bukkit.broadcastMessage(RebootManager.PREFIX + ChatColor.RED + "Reboot has been rescheduled");
        }

        manager.setRebootCommenceTime(Time.now() + ms);
        Bukkit.broadcastMessage(RebootManager.PREFIX + ChatColor.RED + "Reboot has been rescheduled to occur in " + ChatColor.BLUE + Time.convertToRemaining(manager.getTimeUntilReboot()));
        Logger.print(sender.getName() + " rescheduled the reboot to happen in " + Time.convertToRemaining(manager.getTimeUntilReboot()));
    }

    /**
     * Handles printing the time left before a reboot starts
     * @param sender CommandSender
     */
    public void printTime(CommandSender sender) {
        sender.sendMessage(RebootManager.PREFIX + ChatColor.RED + "The server is expected to restart in " + ChatColor.BLUE + Time.convertToRemaining(manager.getTimeUntilReboot()));
    }
}
