package com.playares.essentials.vote;

import com.playares.commons.logger.Logger;
import com.playares.commons.util.general.Configs;
import com.vexsoftware.votifier.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

@AllArgsConstructor
public final class VoteHandler {
    @Getter public final VoteManager manager;

    public void load() {
        if (!manager.getVoteLinks().isEmpty()) {
            manager.getVoteLinks().clear();
        }

        if (!manager.getVoteCommands().isEmpty()) {
            manager.getVoteCommands().clear();
        }

        final YamlConfiguration config = Configs.getConfig(manager.getEssentials().getOwner(), "essentials");

        manager.getVoteLinks().addAll(config.getStringList("vote.links"));
        manager.getVoteCommands().addAll(config.getStringList("vote.issue_commands"));

        Logger.print("Loaded " + manager.getVoteLinks().size() + " Vote Links");
        Logger.print("Loaded " + manager.getVoteCommands().size() + " Vote Commands");
    }

    /**
     * Handles running all Vote commands for the provided Player with the provided Vote
     * @param username Player Name
     * @param vote Votifier Vote
     */
    public void runCommands(String username, Vote vote) {
        for (String command : manager.getVoteCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", username).replace("{service}", vote.getServiceName()));
        }
    }
}