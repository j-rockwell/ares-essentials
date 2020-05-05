package com.playares.essentials;

import com.comphenix.protocol.ProtocolManager;
import com.playares.essentials.broadcast.BroadcastManager;
import com.playares.essentials.kits.KitManager;
import com.playares.essentials.message.MessageManager;
import com.playares.essentials.punishment.PunishmentManager;
import com.playares.essentials.reboot.RebootManager;
import com.playares.essentials.staff.StaffManager;
import com.playares.essentials.support.SupportManager;
import com.playares.essentials.vanish.VanishManager;
import com.playares.essentials.vote.VoteManager;
import com.playares.essentials.warp.WarpManager;
import com.playares.commons.AresPlugin;
import com.playares.commons.AresService;
import com.playares.commons.util.general.Configs;
import com.playares.essentials.command.*;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public final class EssentialsService implements AresService {
    public static final ChatColor PRIMARY = ChatColor.GOLD;
    public static final ChatColor SECONDARY = ChatColor.YELLOW;
    public static final ChatColor SPECIAL = ChatColor.RED;

    @Getter public final AresPlugin owner;
    @Getter public final String name = "Essentials Service";
    @Getter public final String databaseName;

    @Getter protected YamlConfiguration configuration;
    @Getter protected ProtocolManager protocolManager;
    @Getter protected WarpManager warpManager;
    @Getter protected VanishManager vanishManager;
    @Getter protected SupportManager supportManager;
    @Getter protected PunishmentManager punishmentManager;
    @Getter protected MessageManager messageManager;
    @Getter protected BroadcastManager broadcastManager;
    @Getter protected RebootManager rebootManager;
    @Getter protected VoteManager voteManager;
    @Getter protected StaffManager staffManager;
    @Getter protected KitManager kitManager;

    public EssentialsService(AresPlugin owner, String databaseName) {
        this.owner = owner;
        this.databaseName = databaseName;
    }

    public void start() {
        this.configuration = Configs.getConfig(owner, "essentials");

        // Managers
        this.warpManager = new WarpManager(this);
        this.vanishManager = new VanishManager(this);
        this.supportManager = new SupportManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.messageManager = new MessageManager(this);
        this.broadcastManager = new BroadcastManager(this);
        this.rebootManager = new RebootManager(this);
        this.voteManager = new VoteManager(this);
        this.staffManager = new StaffManager(this);
        this.kitManager = new KitManager(this);

        // Commands
        owner.registerCommand(new RebootCommand(this));
        owner.registerCommand(new EssentialsCommand(this));
        owner.registerCommand(new ItemCommand(this));
        owner.registerCommand(new PlayerCommand(this));
        owner.registerCommand(new ChatCommand(this));
        owner.registerCommand(new WarpCommand(this));
        owner.registerCommand(new TeleportCommand(this));
        owner.registerCommand(new ModerationCommand(this));
        owner.registerCommand(new MiscCommand(this));
        owner.registerCommand(new InfoCommand(this));
        owner.registerCommand(new SupportCommand(this));
        owner.registerCommand(new PunishmentCommand(this));
        owner.registerCommand(new MessageCommand(this));
        owner.registerCommand(new IgnoreCommand(this));
        owner.registerCommand(new StaffCommand(this));
        owner.registerCommand(new KitCommand(this));

        // Load Data
        voteManager.getHandler().load();
        warpManager.getHandler().load();
        kitManager.getHandler().load();
        broadcastManager.getHandler().load();
    }

    public void stop() {

    }
}