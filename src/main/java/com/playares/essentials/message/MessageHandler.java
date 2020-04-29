package com.playares.essentials.message;

import com.playares.commons.logger.Logger;
import com.playares.commons.promise.SimplePromise;
import com.playares.commons.services.account.AccountService;
import com.playares.commons.services.account.data.AresAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
public final class MessageHandler {
    @Getter public final MessageManager manager;

    /**
     * Handles sending a new message
     * @param sender Message Sender
     * @param receiverUsername Receiver Username
     * @param message Message
     * @param promise Promise
     */
    public void sendMessage(Player sender, String receiverUsername, String message, SimplePromise promise) {
        final AccountService service = (AccountService)manager.getEssentials().getOwner().getService(AccountService.class);
        final Player receiver = Bukkit.getPlayer(receiverUsername);
        final boolean admin = sender.hasPermission("essentials.message.bypass");

        if (service == null) {
            promise.fail("Failed to obtain Account Service");
            return;
        }

        if (receiver == null) {
            promise.fail("Player not found");
            return;
        }

        if (receiver.getUniqueId().equals(sender.getUniqueId())) {
            promise.fail("You can not message yourself");
            return;
        }

        final AresAccount senderAccount = service.getAccountByBukkitID(sender.getUniqueId());
        final AresAccount receiverAccount = service.getAccountByBukkitID(receiver.getUniqueId());

        if (senderAccount == null) {
            promise.fail("Failed to obtain your account");
            return;
        }

        if (receiverAccount == null) {
            promise.fail("Player not found");
            return;
        }

        if (!senderAccount.getSettings().isPrivateMessagesEnabled()) {
            promise.fail("You have disabled private messages");
            return;
        }

        if (senderAccount.getSettings().isIgnoring(receiver.getUniqueId())) {
            promise.fail("You are ignoring this player");
            return;
        }

        if (!admin && (receiverAccount.getSettings().isIgnoring(sender.getUniqueId()) || !receiverAccount.getSettings().isPrivateMessagesEnabled())) {
            promise.fail("This player has private messages disabled");
            return;
        }

        receiver.sendMessage(ChatColor.GRAY + "(From " + sender.getName() + "): " + ChatColor.RESET + message);
        sender.sendMessage(ChatColor.GRAY + "(To " + receiver.getName() + "): " + ChatColor.RESET + message);

        manager.setRecentlyMessaged(sender, receiver);

        Logger.print(sender.getName() + " -> " + receiver.getName() + ": " + message);

        promise.success();
    }

    /**
     * Handles replying to a previous message
     * @param sender Sender
     * @param message Message
     * @param promise Promise
     */
    public void sendReply(Player sender, String message, SimplePromise promise) {
        final AccountService service = (AccountService)manager.getEssentials().getOwner().getService(AccountService.class);
        final UUID replyId = manager.getRecentlyMessaged(sender);

        if (service == null) {
            promise.fail("Failed to obtain Account Service");
            return;
        }

        if (replyId == null) {
            promise.fail("Nobody has recently messaged you");
            return;
        }

        final Player receiver = Bukkit.getPlayer(replyId);
        final boolean admin = sender.hasPermission("essentials.message.bypass");

        if (receiver == null) {
            promise.fail("Player not found");
            return;
        }

        final AresAccount senderAccount = service.getAccountByBukkitID(sender.getUniqueId());
        final AresAccount receiverAccount = service.getAccountByBukkitID(receiver.getUniqueId());

        if (senderAccount == null) {
            promise.fail("Failed to obtain your account");
            return;
        }

        if (receiverAccount == null) {
            promise.fail("Player not found");
            return;
        }

        if (!senderAccount.getSettings().isPrivateMessagesEnabled()) {
            promise.fail("You have disabled private messages");
            return;
        }

        if (senderAccount.getSettings().isIgnoring(receiver.getUniqueId())) {
            promise.fail("You are ignoring this player");
            return;
        }

        if (!admin && (receiverAccount.getSettings().isIgnoring(sender.getUniqueId()) || !receiverAccount.getSettings().isPrivateMessagesEnabled())) {
            promise.fail("This player has private messages disabled");
            return;
        }

        receiver.sendMessage(ChatColor.GRAY + "(From " + sender.getName() + "): " + ChatColor.RESET + message);
        sender.sendMessage(ChatColor.GRAY + "(To " + receiver.getName() + "): " + ChatColor.RESET + message);

        manager.setRecentlyMessaged(sender, receiver);

        Logger.print(sender.getName() + " -> " + receiver.getName() + ": " + message);

        promise.success();
    }
}
