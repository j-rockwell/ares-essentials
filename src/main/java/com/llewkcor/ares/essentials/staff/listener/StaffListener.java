package com.llewkcor.ares.essentials.staff.listener;

import com.llewkcor.ares.commons.event.PlayerBigMoveEvent;
import com.llewkcor.ares.essentials.staff.StaffManager;
import com.llewkcor.ares.essentials.staff.data.StaffAccount;
import com.llewkcor.ares.essentials.staff.data.StaffDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@AllArgsConstructor
public final class StaffListener implements Listener {
    @Getter public final StaffManager manager;

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        final UUID uniqueId = event.getUniqueId();
        final StaffAccount account = StaffDAO.getAccount(manager.getPlugin().getCore().getDatabaseInstance(), uniqueId);

        if (account != null) {
            manager.getStaffRepository().add(account);
            return;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPermission("essentials.staff")) {
            return;
        }

        StaffAccount account = manager.getAccountByID(player.getUniqueId());

        // The player has the staff rank but hasn't logged in yet
        if (account == null) {
            account = new StaffAccount(player.getUniqueId());

            manager.getStaffRepository().add(account);
            manager.getHandler().saveAccount(player, account);

            player.sendMessage(ChatColor.GRAY + "Before you begin as a Staff member type " + ChatColor.AQUA + "/staff login <password>" + ChatColor.GRAY + " to set your 2FA password");

            return;
        }

        if (account.isEnabled(StaffAccount.StaffSetting.JOIN_VANISHED)) {
            manager.getPlugin().getVanishManager().getHandler().hidePlayer(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final StaffAccount account = manager.getAccountByID(player.getUniqueId());

        if (account == null) {
            return;
        }

        manager.getHandler().saveAccount(player, account);
        manager.getStaffRepository().remove(account);
    }

    @EventHandler
    public void onPlayerMove(PlayerBigMoveEvent event) {
        final Player player = event.getPlayer();
        final StaffAccount account = manager.getAccountByID(player.getUniqueId());

        if (account != null && !account.isVerified()) {
            event.setCancelled(true);
            player.teleport(event.getFrom());

            // Account exists, isn't verified and doesn't have a password
            if (account.getPassword() == null) {
                player.sendMessage(ChatColor.GRAY + "Before you begin as a Staff member type " + ChatColor.AQUA + "/staff login <password>" + ChatColor.GRAY + " to set your 2FA password");
                return;
            }

            player.sendMessage(ChatColor.GRAY + "Please enter your 2FA login with " + ChatColor.AQUA + "/staff login <password>");
        }
    }

    @EventHandler
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final StaffAccount account = manager.getAccountByID(player.getUniqueId());

        if (account != null && !account.isVerified() && !event.getMessage().contains("staff")) {
            event.setCancelled(true);

            // Account exists, isn't verified and doesn't have a password
            if (account.getPassword() == null) {
                player.sendMessage(ChatColor.GRAY + "Before you begin as a Staff member type " + ChatColor.AQUA + "/staff login <password>" + ChatColor.GRAY + " to set your 2FA password");
                return;
            }

            player.sendMessage(ChatColor.GRAY + "Please enter your 2FA login with " + ChatColor.AQUA + "/staff login <password>");
        }
    }
}