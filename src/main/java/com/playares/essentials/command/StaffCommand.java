package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.playares.commons.promise.SimplePromise;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("staff")
@AllArgsConstructor
public final class StaffCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @Subcommand("login|l")
    @Description("Verify your account status")
    @CommandPermission("essentials.staff")
    public void onLogin(Player player, String password) {
        plugin.getStaffManager().getHandler().loginAccount(player, password, new SimplePromise() {
            @Override
            public void success() {
                player.sendMessage(ChatColor.GREEN + "Successfully logged in");
            }

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("settings")
    @Description("Access your staff settings")
    @CommandPermission("essentials.staff")
    public void onSettings(Player player) {
        plugin.getStaffManager().getHandler().openSettingsMenu(player, new SimplePromise() {
            @Override
            public void success() {}

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }
}