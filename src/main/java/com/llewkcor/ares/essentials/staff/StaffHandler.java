package com.llewkcor.ares.essentials.staff;

import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.commons.promise.FailablePromise;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.commons.util.bukkit.Scheduler;
import com.llewkcor.ares.core.player.data.account.AresAccount;
import com.llewkcor.ares.essentials.staff.data.StaffAccount;
import com.llewkcor.ares.essentials.staff.data.StaffDAO;
import com.llewkcor.ares.essentials.staff.menu.StaffSettingMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class StaffHandler {
    @Getter public final StaffManager manager;

    public void saveAccount(Player player, StaffAccount account) {
        new Scheduler(manager.getPlugin()).sync(() -> StaffDAO.saveAccount(manager.getPlugin().getCore().getDatabaseInstance(), account)).run();
    }

    public void openCombatMenu(Player player, SimplePromise promise) {

    }

    /**
     * Handles resetting a players staff account
     * @param username Username
     * @param promise Promise
     */
    public void resetAccount(String username, SimplePromise promise) {
        manager.getPlugin().getCore().getPlayerManager().getAccountByUsername(username, new FailablePromise<AresAccount>() {
            @Override
            public void success(AresAccount aresAccount) {
                if (aresAccount == null) {
                    promise.fail("Player not found");
                    return;
                }

                new Scheduler(manager.getPlugin()).async(() -> {
                    final StaffAccount staffAccount = (manager.getAccountByID(aresAccount.getBukkitId()) != null) ? manager.getAccountByID(aresAccount.getBukkitId()) : StaffDAO.getAccount(manager.getPlugin().getCore().getDatabaseInstance(), aresAccount.getBukkitId());

                    new Scheduler(manager.getPlugin()).sync(() -> {
                        if (staffAccount == null) {
                            promise.fail("Staff account not found");
                            return;
                        }

                        if (staffAccount.getPassword() == null) {
                            promise.fail("Account is already reset");
                            return;
                        }

                        staffAccount.setVerified(false);
                        staffAccount.setPassword(null);

                        final Player resetPlayer = Bukkit.getPlayer(staffAccount.getUniqueId());

                        if (resetPlayer != null && resetPlayer.isOnline()) {
                            resetPlayer.sendMessage(ChatColor.AQUA + "Your staff account has been reset. Please enter new 2FA login credentials to continue.");
                        }

                        Logger.print(aresAccount.getUsername() + "'s Staff Account has been reset");
                        promise.success();
                    }).run();
                }).run();
            }

            @Override
            public void fail(String s) {
                promise.fail(s);
            }
        });
    }

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

        final StaffSettingMenu menu = new StaffSettingMenu(manager.getPlugin(), player, account);
        menu.open();
    }
}
