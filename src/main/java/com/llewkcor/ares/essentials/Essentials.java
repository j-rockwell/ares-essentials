package com.llewkcor.ares.essentials;

import co.aikar.commands.PaperCommandManager;
import com.llewkcor.ares.essentials.command.*;
import com.llewkcor.ares.essentials.warp.WarpManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Essentials extends JavaPlugin {
    public static final ChatColor PRIMARY = ChatColor.GOLD;
    public static final ChatColor SECONDARY = ChatColor.YELLOW;
    public static final ChatColor SPECIAL = ChatColor.RED;

    @Getter protected PaperCommandManager commandManager;
    @Getter protected WarpManager warpManager;

    @Override
    public void onEnable() {
        this.commandManager = new PaperCommandManager(this);
        this.warpManager = new WarpManager(this);

        commandManager.enableUnstableAPI("help");

        commandManager.registerCommand(new ItemCommand(this));
        commandManager.registerCommand(new PlayerCommand(this));
        commandManager.registerCommand(new ChatCommand(this));
        commandManager.registerCommand(new WarpCommand(this));
        commandManager.registerCommand(new TeleportCommand(this));
        commandManager.registerCommand(new ModerationCommand(this));

        warpManager.getHandler().load();
    }

    @Override
    public void onDisable() {

    }
}