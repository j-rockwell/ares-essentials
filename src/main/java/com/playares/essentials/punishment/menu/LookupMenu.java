package com.playares.essentials.punishment.menu;

import com.playares.commons.AresPlugin;
import com.playares.commons.item.ItemBuilder;
import com.playares.commons.menu.ClickableItem;
import com.playares.commons.menu.Menu;
import com.playares.commons.services.account.AccountService;
import com.playares.commons.services.account.data.AresAccount;
import com.playares.commons.services.alts.data.AccountSession;
import com.playares.commons.util.general.IPS;
import com.playares.commons.util.general.Time;
import com.playares.essentials.punishment.data.Punishment;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;

public final class LookupMenu extends Menu {
    @Getter public final AresAccount account;
    @Getter public final Collection<Punishment> punishments;
    @Getter public final Collection<AccountSession> alts;

    public LookupMenu(AresPlugin plugin, Player viewer, AresAccount account, Collection<Punishment> punishmentHistory, Collection<AccountSession> altSessions) {
        super(plugin, viewer, account.getUsername(), 1);
        this.account = account;
        this.punishments = punishmentHistory;
        this.alts = altSessions;

        final AccountService accountService = (AccountService)plugin.getService(AccountService.class);
        final Player viewed = Bukkit.getPlayer(account.getBukkitId());

        final ItemStack webProfileIcon = new ItemBuilder()
                .setMaterial(Material.SIGN)
                .setName(ChatColor.DARK_GREEN + "View Web Profile")
                .addLore(ChatColor.GRAY + "Prints a link to their Ares Web Profile")
                .build();

        final ItemStack accountHistoryIcon = new ItemBuilder()
                .setMaterial(Material.BOOK_AND_QUILL)
                .setName(ChatColor.DARK_PURPLE + "Alternate Accounts (" + ChatColor.LIGHT_PURPLE + alts.size() + ChatColor.DARK_PURPLE + ")")
                .addLore(ChatColor.GRAY + "Click to view a list of all connected accounts")
                .build();

        final ItemStack teleportToIcon = new ItemBuilder()
                .setMaterial(Material.ENDER_PEARL)
                .setName(ChatColor.DARK_AQUA + "Teleport to " + account.getUsername())
                .addLore(ChatColor.GRAY + "Teleport to " + account.getUsername())
                .build();

        final ItemStack punishmentIcon = new ItemBuilder()
                .setMaterial(Material.ENCHANTED_BOOK)
                .setName(ChatColor.DARK_RED + "Punishment History (" + ChatColor.RED + punishmentHistory.size() + ChatColor.DARK_RED + ")")
                .addLore(ChatColor.GRAY + "Click to view " + account.getUsername() + "'s punishment history")
                .build();

        final ItemStack statusIcon = new ItemBuilder()
                .setMaterial(Material.SKULL_ITEM)
                .setName(ChatColor.AQUA + account.getUsername())
                .setData((short)3)
                .addLore(ChatColor.YELLOW + "Status: " + ((viewed != null && viewed.isOnline()) ? ChatColor.GREEN + "Online" : ChatColor.RED + "Offline"))
                .addLore(ChatColor.YELLOW + "Account Created: " + ChatColor.GRAY + Time.convertToDate(new Date(account.getInitialLogin())))
                .addLore(ChatColor.YELLOW + "Last Seen: " + ChatColor.GRAY + Time.convertToDate(new Date(account.getLastLogin())))
                .build();

        addItem(new ClickableItem(webProfileIcon, 0, click -> {

            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "https://playares.com/account/" + account.getUsername() + "/");

        }));

        addItem(new ClickableItem(accountHistoryIcon, 2, click -> {

            player.closeInventory();

            if (accountService == null) {
                player.sendMessage(ChatColor.RED + "Account service not found");
                return;
            }

            alts.forEach(alt -> {
                accountService.getAccountByBukkitID(alt.getBukkitId(), aresAccount -> {
                    player.sendMessage(ChatColor.DARK_PURPLE + "MC UUID" + ChatColor.LIGHT_PURPLE + ": " + alt.getBukkitId().toString());
                    player.sendMessage(ChatColor.DARK_PURPLE + "Username" + ChatColor.LIGHT_PURPLE + ": " + (aresAccount != null ? aresAccount.getUsername() : "Not found"));
                    player.sendMessage(ChatColor.DARK_PURPLE + "IP Address" + ChatColor.LIGHT_PURPLE + ": " + IPS.toString(alt.getAddress()));
                    player.sendMessage(ChatColor.DARK_PURPLE + "Initial Login" + ChatColor.LIGHT_PURPLE + ": " + Time.convertToDate(new Date(alt.getFirstSeen())));
                    player.sendMessage(ChatColor.DARK_PURPLE + "Last Login" + ChatColor.LIGHT_PURPLE + ": " + Time.convertToDate(new Date(alt.getLastSeen())));
                    player.sendMessage(ChatColor.RESET + " ");
                });
            });
        }));

        addItem(new ClickableItem(teleportToIcon, 4, click -> {

            player.closeInventory();

            if (viewed == null || !viewed.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player not online");
                return;
            }

            player.teleport(viewed);
            player.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.GOLD + viewed.getName());

        }));

        addItem(new ClickableItem(punishmentIcon, 6, click -> {

            player.closeInventory();

            punishments.forEach(punishment -> {

                player.sendMessage(ChatColor.RED + punishment.getPunishmentType().name());
                player.sendMessage(ChatColor.GRAY + "Created" + ChatColor.WHITE + ": " + Time.convertToDate(new Date()) + ChatColor.GRAY + ", Expires" + ChatColor.WHITE + ": " + (punishment.isForever() ? "never" : Time.convertToDate(new Date(punishment.getExpireDate()))));
                player.sendMessage(ChatColor.GRAY + "Reason" + ChatColor.WHITE + ": " + punishment.getReason());
                player.sendMessage(ChatColor.GRAY + "Appealed" + ChatColor.WHITE + ": " + (punishment.isAppealed() ? ChatColor.GREEN + "Appealed" : ChatColor.RED + "No"));
                player.sendMessage(ChatColor.RESET + " ");

            });

        }));

        addItem(new ClickableItem(statusIcon, 8, click -> {}));
    }
}