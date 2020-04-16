package com.llewkcor.ares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.essentials.Essentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("staff")
@AllArgsConstructor
public final class StaffCommand extends BaseCommand {
    @Getter public final Essentials plugin;

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

    @Subcommand("reset")
    @Description("Reset a staff members 2FA login")
    @CommandPermission("essentials.staff.reset")
    @Syntax("<username>")
    @CommandCompletion("@players")
    public void onReset(CommandSender sender, String username) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "This command can only be ran via console");
        }

        plugin.getStaffManager().getHandler().resetAccount(username, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage("Account has been successfully reset");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(s);
            }
        });
    }
}