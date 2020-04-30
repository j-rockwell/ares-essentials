package com.playares.essentials.staff;

import com.playares.commons.util.bukkit.Players;
import com.playares.essentials.staff.data.StaffAccount;
import com.playares.essentials.staff.menu.StaffSettingMenu;
import com.playares.commons.logger.Logger;
import com.playares.commons.promise.SimplePromise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class StaffHandler {
    @Getter public final StaffManager manager;

    /**
     * Handles logging in to account
     * @param player Player
     * @param password Password
     * @param promise Promise
     */
    public void loginAccount(Player player, String password, SimplePromise promise) {
        final StaffAccount account = manager.getAccountByID(player.getUniqueId());

        if (account == null) {
            promise.fail("Account not found");
            return;
        }

        if (account.isVerified()) {
            promise.fail("You are already signed in");
            return;
        }

        if (account.getPassword() != null && !account.isPassword(password)) {
            Logger.warn(player.getName() + " failed to enter a matching password for their staff account (IP: " + player.getAddress().getAddress().getHostAddress() + ")");
            promise.fail("Password does not match");
            return;
        }

        if (account.getPassword() == null) {
            account.setPassword(password);
            account.setVerified(true);
            Logger.print(player.getName() + " updated their staff password");
            promise.success();
            return;
        }

        account.setVerified(true);
        Logger.print(player.getName() + " authenticated their staff account");

        if (account.isEnabled(StaffAccount.StaffSetting.SHOW_TICKET_NOTIFICATIONS)) {
            manager.getEssentials().getSupportManager().getReports(reports -> {
                if (!reports.isEmpty() && player.isOnline()) {
                    player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.RED + "Report" + ChatColor.DARK_RED + "] " + ChatColor.RED + "There's " + ChatColor.DARK_RED + reports.size() + ChatColor.RED + " open report(s)");
                    player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.RED + "/reports" + ChatColor.YELLOW + " to view");
                    Players.playSound(player, Sound.NOTE_PIANO);
                }
            });

            manager.getEssentials().getSupportManager().getRequests(requests -> {
                if (!requests.isEmpty() && player.isOnline()) {
                    player.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.AQUA + "Request" + ChatColor.DARK_AQUA + "] " + ChatColor.AQUA + "There's " + ChatColor.BLUE + requests.size() + ChatColor.AQUA + " open request(s)");
                    player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.AQUA + "/requests" + ChatColor.YELLOW + " to view");
                    Players.playSound(player, Sound.NOTE_PIANO);
                }
            });
        }

        promise.success();
    }

    /**
     * Handles opening the Staff Settings Menu
     * @param player Player
     * @param promise Promise
     */
    public void openSettingsMenu(Player player, SimplePromise promise) {
        final StaffAccount account = manager.getAccountByID(player.getUniqueId());

        if (account == null) {
            promise.fail("Failed to obtain your staff account");
            return;
        }

        if (!player.hasPermission("essentials.staff")) {
            return;
        }

        final StaffSettingMenu menu = new StaffSettingMenu(manager.getEssentials(), player, account);
        menu.open();
    }
}
