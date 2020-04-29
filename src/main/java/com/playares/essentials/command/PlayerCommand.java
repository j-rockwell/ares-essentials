package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.playares.commons.logger.Logger;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class PlayerCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @CommandAlias("clear|clearinv")
    @Description("Clear your inventory")
    @CommandCompletion("@players")
    @CommandPermission("essentials.clearinv")
    @Syntax("[username]")
    public void onClear(CommandSender sender, @Optional String username) {
        if (!(sender instanceof Player) && username == null) {
            sender.sendMessage(ChatColor.RED + "Username required");
            return;
        }

        if (username == null) {
            final Player player = (Player)sender;

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.sendMessage(EssentialsService.PRIMARY + "Cleared inventory");

            Logger.print(player.getName() + " (" + player.getUniqueId().toString() + ") cleared their inventory");

            return;
        }

        final Player player = Bukkit.getPlayer(username);

        if (player == null || !player.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.sendMessage(EssentialsService.PRIMARY + "Your inventory has been cleared by " + EssentialsService.SECONDARY + sender.getName());

        Logger.print(sender.getName() + " cleared " + player.getName() + "'s inventory");
    }

    @CommandAlias("heal")
    @Description("Heal yourself")
    @CommandCompletion("@players")
    @CommandPermission("essentials.heal")
    @Syntax("[username]")
    public void onHeal(CommandSender sender, @Optional String username) {
        if (!(sender instanceof Player) && username == null) {
            sender.sendMessage(ChatColor.RED + "Username required");
            return;
        }

        if (username == null) {
            final Player player = (Player)sender;

            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setExhaustion(0);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            player.sendMessage(EssentialsService.PRIMARY + "You have been healed");

            Logger.print(player.getName() + " (" + player.getUniqueId().toString() + ") healed themselves");

            return;
        }

        final Player player = Bukkit.getPlayer(username);

        if (player == null || !player.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExhaustion(0);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.sendMessage(EssentialsService.PRIMARY + "You have been healed by " + EssentialsService.SECONDARY + sender.getName());

        Logger.print(sender.getName() + " healed " + player.getName());
    }

    @CommandAlias("gamemode|gm")
    @Syntax("<gamemode> [username]")
    @Description("Change your gamemode")
    @CommandPermission("essentials.gamemode")
    public void onGamemode(CommandSender sender, @Values("survival|s|0|creative|c|1|adventure|a|2|spectator|spec|3") String gamemodeName, @Optional String username) {
        GameMode gamemode;

        if (gamemodeName.equalsIgnoreCase("survival") || gamemodeName.equalsIgnoreCase("s") || gamemodeName.equalsIgnoreCase("0")) {
            gamemode = GameMode.SURVIVAL;
        } else if (gamemodeName.equalsIgnoreCase("creative") || gamemodeName.equalsIgnoreCase("c") || gamemodeName.equalsIgnoreCase("1")) {
            gamemode = GameMode.CREATIVE;
        } else if (gamemodeName.equalsIgnoreCase("adventure") || gamemodeName.equalsIgnoreCase("a") || gamemodeName.equalsIgnoreCase("2")) {
            gamemode = GameMode.ADVENTURE;
        } else if (gamemodeName.equalsIgnoreCase("spectator") || gamemodeName.equalsIgnoreCase("spec") || gamemodeName.equalsIgnoreCase("3")) {
            gamemode = GameMode.SPECTATOR;
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid gamemode");
            return;
        }

        if (!(sender instanceof Player) && username == null) {
            sender.sendMessage(ChatColor.RED + "Username required");
            return;
        }

        if (username == null) {
            final Player player = (Player)sender;

            player.setGameMode(gamemode);
            player.sendMessage(EssentialsService.PRIMARY + "Your gamemode has been changed to " + EssentialsService.SECONDARY + StringUtils.capitalize(gamemode.name().toLowerCase()));

            Logger.print(player.getName() + " (" + player.getUniqueId().toString() + ") changed their gamemode to " + gamemode.name());

            return;
        }

        final Player player = Bukkit.getPlayer(username);

        if (player == null || !player.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        player.setGameMode(gamemode);
        player.sendMessage(EssentialsService.PRIMARY + "Your gamemode has been changed to " + EssentialsService.SECONDARY + StringUtils.capitalize(gamemode.name().toLowerCase()));

        Logger.print(sender.getName() + " changed " + player.getName() + "'s gamemode to " + gamemode.name());
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}