package com.llewkcor.ares.essentials.punishment.menu;

import com.google.common.collect.Lists;
import com.llewkcor.ares.commons.item.ItemBuilder;
import com.llewkcor.ares.commons.menu.ClickableItem;
import com.llewkcor.ares.commons.menu.Menu;
import com.llewkcor.ares.commons.promise.FailablePromise;
import com.llewkcor.ares.commons.util.general.IPS;
import com.llewkcor.ares.commons.util.general.Time;
import com.llewkcor.ares.core.alts.data.AltEntry;
import com.llewkcor.ares.core.player.data.account.AresAccount;
import com.llewkcor.ares.essentials.Essentials;
import com.llewkcor.ares.essentials.punishment.data.Punishment;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public final class PlayerLookupMenu extends Menu {
    public PlayerLookupMenu(Essentials plugin, Player player, AresAccount observed, Collection<Punishment> punishments, Collection<AltEntry> altEntries) {
        super(plugin, player, observed.getUsername(), 1);

        final List<String> aboutLore = Lists.newArrayList();

        aboutLore.add(ChatColor.YELLOW + "Connected" + ChatColor.GRAY + ": " + (Bukkit.getPlayer(observed.getBukkitId()) != null ? ChatColor.GREEN + "Connected" : ChatColor.RED + "Disconnected"));
        aboutLore.add(ChatColor.YELLOW + "Account Created" + ChatColor.GRAY + ": " + Time.convertToDate(new Date(observed.getInitialLogin())));
        aboutLore.add(ChatColor.YELLOW + "Last Seen" + ChatColor.GRAY + ": " + Time.convertToDate(new Date(observed.getLastLogin())));

        final ItemStack webProfileIcon = new ItemBuilder()
                .setMaterial(Material.SIGN)
                .setName(ChatColor.DARK_GREEN + "View Web Profile")
                .addLore(ChatColor.GRAY + "Prints link to their Ares web profile")
                .build();

        final ItemStack accountHistoryIcon = new ItemBuilder()
                .setMaterial(Material.BOOK_AND_QUILL)
                .setName(ChatColor.DARK_PURPLE + "Account History (" + ChatColor.LIGHT_PURPLE + altEntries.size() + ChatColor.DARK_PURPLE + ")")
                .addLore(ChatColor.GRAY + "Prints the user account history")
                .build();

        final ItemStack teleportToIcon = new ItemBuilder()
                .setMaterial(Material.ENDER_PEARL)
                .setName(ChatColor.DARK_AQUA + "Teleport To")
                .addLore(ChatColor.GRAY + "Teleports to the player")
                .build();

        final ItemStack punishmentIcon = new ItemBuilder()
                .setMaterial(Material.BOOK_AND_QUILL)
                .setName(ChatColor.YELLOW + "Punishment History (" + ChatColor.RED + punishments.size() + ChatColor.YELLOW + ")")
                .addLore(ChatColor.GRAY + "Prints the user punishment history")
                .build();

        final ItemStack aboutIcon = new ItemBuilder()
                .setMaterial(Material.SKULL_ITEM)
                .setData((short)3)
                .setName(ChatColor.AQUA + "About")
                .addLore(aboutLore)
                .build();

        addItem(new ClickableItem(webProfileIcon, 0, click -> {
            player.closeInventory();
            player.sendMessage(ChatColor.DARK_GREEN + observed.getUsername() + "'s Profile Link" + ChatColor.GREEN + ": https://playares.com/user/" + observed.getUniqueId().toString());
        }));

        addItem(new ClickableItem(accountHistoryIcon, 2, click -> {
            player.closeInventory();

            for (AltEntry entry : altEntries) {
                plugin.getCore().getPlayerManager().getAccountByBukkitID(entry.getUniqueId(), new FailablePromise<AresAccount>() {
                    @Override
                    public void success(AresAccount aresAccount) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "UUID" + ChatColor.LIGHT_PURPLE + ": " + aresAccount.getBukkitId().toString());
                        player.sendMessage(ChatColor.DARK_PURPLE + "Username" + ChatColor.LIGHT_PURPLE + ": " + aresAccount.getUsername());
                        player.sendMessage(ChatColor.DARK_PURPLE + "IP Address" + ChatColor.LIGHT_PURPLE + ": " + IPS.toString(entry.getAddress()));
                        player.sendMessage(ChatColor.DARK_PURPLE + "First Seen" + ChatColor.LIGHT_PURPLE + ": " + Time.convertToElapsed(Time.now() - entry.getCreate()));
                        player.sendMessage(ChatColor.DARK_PURPLE + "Last Seen" + ChatColor.LIGHT_PURPLE + ": " + Time.convertToElapsed(Time.now() - entry.getLastSeen()));
                        player.sendMessage(ChatColor.RESET + " ");
                    }

                    @Override
                    public void fail(String s) {
                        player.sendMessage(ChatColor.RED + "Skipped Alt Entry for " + entry.getUniqueId().toString() + "... Ares Account not found");
                    }
                });
            }
        }));

        addItem(new ClickableItem(teleportToIcon, 4, click -> {
            final Player to = Bukkit.getPlayer(observed.getBukkitId());

            player.closeInventory();

            if (to == null || !to.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            player.teleport(to);
        }));

        addItem(new ClickableItem(punishmentIcon, 6, click -> {
            player.closeInventory();

            punishments.forEach(punishment -> {
                player.sendMessage(ChatColor.RED + StringUtils.capitalize(punishment.getPunishmentType().name().toLowerCase()));
                player.sendMessage(ChatColor.GRAY + "Created" + ChatColor.RESET + ": " + Time.convertToDate(new Date(punishment.getCreateDate())) + ChatColor.GRAY + ", Expires" + ChatColor.RESET + ": " + (punishment.isForever() ? "never" : Time.convertToDate(new Date(punishment.getExpireDate()))));
                player.sendMessage(ChatColor.GRAY + "Reason" + ChatColor.RESET + ": " + punishment.getReason());
                player.sendMessage(ChatColor.GRAY + "Appealed" + ChatColor.RESET + ": " + (punishment.isAppealed() ? "Yes" : "No"));
                player.sendMessage(ChatColor.RESET + " ");
            });
        }));

        addItem(new ClickableItem(aboutIcon, 8, click -> {}));
    }
}
