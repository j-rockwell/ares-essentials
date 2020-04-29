package com.playares.essentials.vote;

import com.google.common.collect.Lists;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.vote.listener.VotifierListener;
import com.playares.commons.util.general.Configs;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public final class VoteManager {
    @Getter public final EssentialsService essentials;
    @Getter public final VoteHandler handler;

    @Getter public final List<String> voteLinks;
    @Getter public final List<String> voteCommands;

    public VoteManager(EssentialsService essentials) {
        this.essentials = essentials;
        this.handler = new VoteHandler(this);
        this.voteLinks = Lists.newArrayList();
        this.voteCommands = Lists.newArrayList();

        final YamlConfiguration config = Configs.getConfig(essentials.getOwner(), "config");

        voteLinks.addAll(config.getStringList("vote.links"));
        voteCommands.addAll(config.getStringList("vote.issue_commands"));

        essentials.getOwner().registerListener(new VotifierListener(this));
    }
}
