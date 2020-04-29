package com.playares.essentials.vote;

import com.vexsoftware.votifier.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;

@AllArgsConstructor
public final class VoteHandler {
    @Getter public final VoteManager manager;

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