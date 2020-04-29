package com.playares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.playares.commons.logger.Logger;
import com.playares.essentials.EssentialsService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class MiscCommand extends BaseCommand {
    @Getter public final EssentialsService plugin;

    @CommandAlias("vote")
    @Description("Receive the voting links for this server")
    public void onVote(Player player) {
        player.sendMessage(ChatColor.RESET + " ");
        player.sendMessage(EssentialsService.PRIMARY + "Vote using the following links and receive a " + EssentialsService.SECONDARY + "Vote Crate");

        for (String link : plugin.getVoteManager().getVoteLinks()) {
            player.sendMessage(EssentialsService.SPECIAL + link);
            player.sendMessage(ChatColor.RESET + " ");
        }
    }

    @CommandAlias("weather")
    @CommandPermission("essentials.weather")
    @Description("Change the weather for the world you're in")
    @Syntax("<clear/rain/thunder>")
    public void onWeather(Player player, @Values("clear|rain|thunder") String weatherName) {
        final World world = player.getWorld();

        if (weatherName.equalsIgnoreCase("clear") || weatherName.equalsIgnoreCase("sun")) {
            world.setStorm(false);
            world.setThundering(false);
            world.setWeatherDuration(Integer.MAX_VALUE);

            player.sendMessage(EssentialsService.PRIMARY + "Weather state changed to " + EssentialsService.SECONDARY + "clear");
            Logger.print(player.getName() + " changed the weather of " + world.getName() + " to clear");
        }

        else if (weatherName.equalsIgnoreCase("rain")) {
            world.setStorm(true);
            world.setThundering(false);
            world.setWeatherDuration(Integer.MAX_VALUE);

            player.sendMessage(EssentialsService.PRIMARY + "Weather state changed to " + EssentialsService.SECONDARY + "storm");
            Logger.print(player.getName() + " changed the weather of " + world.getName() + " to storm");
        }

        else if (weatherName.equalsIgnoreCase("thunder")) {
            world.setStorm(true);
            world.setThundering(true);
            world.setThunderDuration(Integer.MAX_VALUE);
            world.setWeatherDuration(Integer.MAX_VALUE);

            player.sendMessage(EssentialsService.PRIMARY + "Weather state changed to " + EssentialsService.SECONDARY + "thundering");
            Logger.print(player.getName() + " changed the weather of " + world.getName() + " to thunder");
        }

        else {
            player.sendMessage(ChatColor.RED + "Invalid weather state");
        }
    }
}
