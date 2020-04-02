package com.llewkcor.ares.essentials;

import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.llewkcor.ares.essentials.command.*;
import com.llewkcor.ares.essentials.support.SupportManager;
import com.llewkcor.ares.essentials.vanish.VanishManager;
import com.llewkcor.ares.essentials.warp.WarpManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Essentials extends JavaPlugin {
    public static final ChatColor PRIMARY = ChatColor.GOLD;
    public static final ChatColor SECONDARY = ChatColor.YELLOW;
    public static final ChatColor SPECIAL = ChatColor.RED;

    @Getter protected ProtocolManager protocolManager;

    @Getter protected PaperCommandManager commandManager;
    @Getter protected WarpManager warpManager;
    @Getter protected VanishManager vanishManager;
    @Getter protected SupportManager supportManager;

    @Override
    public void onEnable() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        this.commandManager = new PaperCommandManager(this);
        this.warpManager = new WarpManager(this);
        this.vanishManager = new VanishManager(this);
        this.supportManager = new SupportManager(this);

        commandManager.enableUnstableAPI("help");

        commandManager.registerCommand(new ItemCommand(this));
        commandManager.registerCommand(new PlayerCommand(this));
        commandManager.registerCommand(new ChatCommand(this));
        commandManager.registerCommand(new WarpCommand(this));
        commandManager.registerCommand(new TeleportCommand(this));
        commandManager.registerCommand(new ModerationCommand(this));
        commandManager.registerCommand(new MiscCommand(this));
        commandManager.registerCommand(new InfoCommand(this));
        commandManager.registerCommand(new SupportCommand(this));

        warpManager.getHandler().load();
    }

    @Override
    public void onDisable() {

    }
}