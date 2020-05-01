package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.playares.commons.promise.SimplePromise;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("kit|kits")
@AllArgsConstructor
public final class KitCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @Subcommand("list")
    @CommandPermission("essentials.kit")
    @Description("List all kits")
    public void onList(Player player) {
        plugin.getKitManager().getHandler().list(player);
    }

    @Subcommand("save")
    @CommandPermission("essentials.kit.edit")
    @Description("Save your current inventory as a kit")
    public void onSave(Player player, String kitName) {
        plugin.getKitManager().getHandler().save(player, kitName, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(ChatColor.GREEN + "Kit saved");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("give|load")
    @CommandPermission("essentials.kit")
    @Description("Load a kit")
    public void onGive(Player player, String kitName) {
        plugin.getKitManager().getHandler().give(player, kitName, new SimplePromise() {
            @Override
            public void success() {}

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("givenear|near")
    @CommandPermission("essentials.kit")
    @Description("Load a kit for nearby players")
    public void onGiveNear(Player player, String kitName) {
        plugin.getKitManager().getHandler().giveNear(player, kitName, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(ChatColor.GREEN + "Kits applied successfully");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("scroll")
    @CommandPermission("essentials.kit")
    @Description("Give yourself a kit scroll")
    public void onScroll(Player player, String kitName) {
        plugin.getKitManager().getHandler().giveScroll(player, kitName, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(ChatColor.GREEN + "You have been given a Kit Scroll");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("delete|del")
    @CommandPermission("essentials.kit.edit")
    @Description("Delete a kit")
    public void onDelete(Player player, String kitName) {
        plugin.getKitManager().getHandler().delete(kitName, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(ChatColor.GREEN + "Kit has been deleted");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }
}