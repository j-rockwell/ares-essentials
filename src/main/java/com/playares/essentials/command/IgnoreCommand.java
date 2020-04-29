package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import com.playares.commons.logger.Logger;
import com.playares.essentials.EssentialsService;
import com.playares.commons.services.account.AccountService;
import com.playares.commons.services.account.data.AresAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class IgnoreCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @CommandAlias("ignore|block")
    @Description("Hide messages from a player")
    @Syntax("<username>")
    public void onIgnore(Player player, String username) {
        final AccountService service = (AccountService)plugin.getOwner().getService(AccountService.class);

        if (service == null) {
            player.sendMessage(ChatColor.RED + "Failed to obtain Account Service");
            return;
        }

        final AresAccount account = service.getAccountByBukkitID(player.getUniqueId());

        service.getAccountByUsername(username, ignoredAccount -> {
            if (ignoredAccount == null) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            if (account.getSettings().isIgnoring(ignoredAccount.getBukkitId())) {
                player.sendMessage(ChatColor.RED + "You are already ignoring " + ignoredAccount.getUsername());
                return;
            }

            account.getSettings().getIgnoredPlayers().add(ignoredAccount.getBukkitId());
            player.sendMessage(ChatColor.GREEN + "You are now ignoring " + ignoredAccount.getUsername());

            Logger.print(player.getName() + " started ignoring " + ignoredAccount.getUsername());
        });
    }

    @CommandAlias("unignore|unblock")
    @Description("Show messages from a player you've previously blocked")
    @Syntax("<username>")
    public void onUnignore(Player player, String username) {
        final AccountService service = (AccountService)plugin.getOwner().getService(AccountService.class);

        if (service == null) {
            player.sendMessage(ChatColor.RED + "Failed to obtain Account Service");
            return;
        }

        final AresAccount account = service.getAccountByBukkitID(player.getUniqueId());

        service.getAccountByUsername(username, ignoredAccount -> {
            if (ignoredAccount == null) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return;
            }

            if (!account.getSettings().isIgnoring(ignoredAccount.getBukkitId())) {
                player.sendMessage(ChatColor.RED + "You are not ignoring " + ignoredAccount.getUsername());
                return;
            }

            account.getSettings().getIgnoredPlayers().remove(ignoredAccount.getBukkitId());
            player.sendMessage(ChatColor.GREEN + "You are no longer ignoring " + ignoredAccount.getUsername());

            Logger.print(player.getName() + " stopped ignoring " + ignoredAccount.getUsername());
        });
    }
}
