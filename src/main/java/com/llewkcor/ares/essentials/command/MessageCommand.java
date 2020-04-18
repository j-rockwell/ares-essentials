package com.llewkcor.ares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import com.llewkcor.ares.commons.promise.SimplePromise;
import com.llewkcor.ares.essentials.Essentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class MessageCommand extends BaseCommand {
    @Getter public final Essentials plugin;

    @CommandAlias("message|msg|tell")
    @Description("Send a player a private message")
    @Syntax("<username> <message>")
    public void onMessage(Player player, String username, String message) {
        plugin.getMessageManager().getHandler().sendMessage(player, username, message, new SimplePromise() {
            @Override
            public void success() {}

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }

    @CommandAlias("reply|r")
    @Description("Reply to your most recently received message")
    @Syntax("<message>")
    public void onReply(Player player, String message) {
        plugin.getMessageManager().getHandler().sendReply(player, message, new SimplePromise() {
            @Override
            public void success() {}

            @Override
            public void fail(String s) {
                player.sendMessage(ChatColor.RED + s);
            }
        });
    }
}