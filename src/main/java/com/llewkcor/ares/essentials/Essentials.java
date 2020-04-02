package com.llewkcor.ares.essentials;

import co.aikar.commands.PaperCommandManager;
import com.llewkcor.ares.essentials.command.ItemCommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Essentials extends JavaPlugin {
    public static final ChatColor PRIMARY = ChatColor.GOLD;
    public static final ChatColor SECONDARY = ChatColor.YELLOW;
    public static final ChatColor SPECIAL = ChatColor.RED;

    @Getter protected PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        this.commandManager = new PaperCommandManager(this);

        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new ItemCommand(this));
    }

    @Override
    public void onDisable() {

    }
}