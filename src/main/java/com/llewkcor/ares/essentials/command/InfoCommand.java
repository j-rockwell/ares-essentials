package com.llewkcor.ares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.llewkcor.ares.essentials.Essentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public final class InfoCommand extends BaseCommand {
    @Getter public final Essentials plugin;

    @CommandAlias("list")
    @Description("View a list of online players")
    public void onList(Player player) {
        final List<String> usernames = Lists.newArrayList();

        Bukkit.getOnlinePlayers().stream().filter(online -> !plugin.getVanishManager().isVanished(online)).forEach(online -> usernames.add(online.getName()));

        usernames.sort(String.CASE_INSENSITIVE_ORDER);

        player.sendMessage(Essentials.PRIMARY + "Online Users (" + Essentials.SECONDARY + usernames.size() + Essentials.PRIMARY + ")");
        player.sendMessage(ChatColor.RESET + Joiner.on(", ").join(usernames));
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}
