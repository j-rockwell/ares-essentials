package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.playares.commons.logger.Logger;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class TeleportCommand extends BaseCommand {
    @Getter public EssentialsService plugin;

    @CommandAlias("teleport|tp")
    @Description("Teleport to a player")
    @CommandPermission("essentials.tp")
    @CommandCompletion("@players")
    public void onTeleport(Player from, String toName) {
        final Player to = Bukkit.getPlayer(toName);

        if (to == null || !to.isOnline()) {
            from.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        from.teleport(to.getLocation());
        from.sendMessage(EssentialsService.PRIMARY + "Teleported to " + EssentialsService.SECONDARY + to.getName());
        Logger.print(from.getName() + " teleported to " + to.getName());
    }

    @CommandAlias("teleport|tp")
    @Description("Teleport to a player")
    @CommandPermission("essentials.tp")
    public void onTeleport(CommandSender sender, String fromName, String toName) {
        final Player from = Bukkit.getPlayer(fromName);
        final Player to = Bukkit.getPlayer(toName);

        if (from == null || !from.isOnline() || to == null || !to.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return;
        }

        if (from.getUniqueId().equals(to.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Can not teleport player to self");
            return;
        }

        from.teleport(to.getLocation());
        from.sendMessage(EssentialsService.PRIMARY + "You have been teleported to " + EssentialsService.SECONDARY + to.getName());
        Logger.print(sender.getName() + " teleported " + from.getName() + " to " + to.getName());
    }

    @CommandAlias("teleport|tp")
    @Description("Teleport to specified coordinates")
    @CommandPermission("essentials.tp")
    public void onTeleport(Player player, double x, double y, double z) {
        final Location location = new Location(player.getWorld(), x, y, z);
        player.teleport(location);
        player.sendMessage(EssentialsService.PRIMARY + "Teleported to X: " + EssentialsService.SECONDARY + location.getX() + EssentialsService.PRIMARY + ", Y: " + EssentialsService.SECONDARY + location.getY() + EssentialsService.PRIMARY + ", Z: " + EssentialsService.SECONDARY + location.getZ());
        Logger.print(player.getName() + " teleported to x: " + x + ", y: " + y + ", z: " + z);
    }

    @CommandAlias("teleport|tp")
    @Description("Teleport to specified coordinates")
    @CommandPermission("essentials.tp")
    public void onTeleport(Player player, double x, double y, double z, String worldName) {
        final World world = Bukkit.getWorld(worldName);

        if (world == null) {
            player.sendMessage(ChatColor.RED + "World not found");
            return;
        }

        final Location location = new Location(world, x, y, z);
        player.teleport(location);
        player.sendMessage(EssentialsService.PRIMARY + "Teleported to X: " + EssentialsService.SECONDARY + location.getX() + EssentialsService.PRIMARY + ", Y: " + EssentialsService.SECONDARY + location.getY() + EssentialsService.PRIMARY + ", Z: " + EssentialsService.SECONDARY + location.getZ());
        Logger.print(player.getName() + " teleported to x: " + x + ", y: " + y + ", z: " + z);
    }

    @CommandAlias("world")
    @Description("Change worlds")
    @CommandPermission("essentials.tp")
    public void onWorld(Player player, String worldName) {
        final World world = Bukkit.getWorld(worldName);

        if (world == null) {
            player.sendMessage(ChatColor.RED + "World not found");
            return;
        }

        player.teleport(world.getSpawnLocation());
        player.sendMessage(EssentialsService.PRIMARY + "Changed world to " + EssentialsService.SECONDARY + world.getName());
        Logger.print(player.getName() + " changed their world to " + world.getName());
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}