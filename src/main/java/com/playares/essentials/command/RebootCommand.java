package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.playares.commons.promise.SimplePromise;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@CommandAlias("reboot")
public final class RebootCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @Subcommand("time")
    @Description("See when the next reboot is scheduled to happen")
    public void onTime(CommandSender sender) {
        plugin.getRebootManager().getHandler().printTime(sender);
    }

    @Subcommand("schedule")
    @Description("Schedule the next reboot")
    @CommandPermission("essentials.reboot.schedule")
    @Syntax("<time>")
    public void onSchedule(CommandSender sender, String timeName) {
        plugin.getRebootManager().getHandler().scheduleReboot(sender, timeName, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Reboot has been rescheduled successfully");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("start")
    @Description("Start a reboot")
    @CommandPermission("essentials.reboot.schedule")
    @Syntax("<time>")
    public void onStart(CommandSender sender, @Optional String timeName) {
        if (timeName != null) {
            plugin.getRebootManager().getHandler().startReboot(sender, timeName, new SimplePromise() {
                @Override
                public void success() {
                    sender.sendMessage(ChatColor.GREEN + "Reboot process has started successfully");
                }

                @Override
                public void fail(String s) {
                    sender.sendMessage(ChatColor.RED + s);
                }
            });

            return;
        }

        plugin.getRebootManager().getHandler().startReboot(sender, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Reboot process has been started successfully");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @Subcommand("cancel|stop")
    @Description("Stop a reboot process")
    @CommandPermission("essentials.reboot.schedule")
    public void onStop(CommandSender sender) {
        plugin.getRebootManager().getHandler().cancelReboot(sender, new SimplePromise() {
            @Override
            public void success() {
                sender.sendMessage(ChatColor.GREEN + "Reboot process has been cancelled successfully");
            }

            @Override
            public void fail(String s) {
                sender.sendMessage(ChatColor.RED + s);
            }
        });
    }
}