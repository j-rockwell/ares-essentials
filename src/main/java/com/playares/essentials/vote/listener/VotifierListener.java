package com.playares.essentials.vote.listener;

import com.playares.essentials.vote.VoteManager;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@AllArgsConstructor
public final class VotifierListener implements Listener {
    @Getter public final VoteManager manager;

    @EventHandler
    public void onVote(VotifierEvent event) {
        final Vote vote = event.getVote();
        final String username = vote.getUsername();
        manager.getHandler().runCommands(username, vote);
    }
}