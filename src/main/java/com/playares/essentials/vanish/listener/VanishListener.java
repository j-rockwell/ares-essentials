package com.playares.essentials.vanish.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.collect.Lists;
import com.playares.essentials.staff.data.StaffAccount;
import com.playares.essentials.vanish.VanishManager;
import com.playares.essentials.vanish.menu.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public final class VanishListener implements Listener {
    public final VanishManager manager;

    public VanishListener(VanishManager manager) {
        this.manager = manager;

        // Listeners for general entity packets
        manager.getEssentials().getOwner().getProtocolManager().addPacketListener(new PacketAdapter(manager.getEssentials().getOwner(),
                ListenerPriority.MONITOR, PacketType.Play.Server.ANIMATION, PacketType.Play.Server.BLOCK_BREAK_ANIMATION,
                PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.ENTITY, PacketType.Play.Server.REL_ENTITY_MOVE,
                PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_LOOK,
                PacketType.Play.Server.ENTITY_HEAD_ROTATION, PacketType.Play.Server.ENTITY_METADATA,
                PacketType.Play.Server.ATTACH_ENTITY, PacketType.Play.Server.ENTITY_VELOCITY,
                PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.ENTITY_TELEPORT,
                PacketType.Play.Server.ENTITY_EFFECT, PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {

            @Override
            public void onPacketSending(PacketEvent event) {
                final Player receiver = event.getPlayer();
                final PacketContainer packet = event.getPacket();

                if (receiver.hasPermission("essentials.vanish")) {
                    return;
                }

                if (packet.getType().equals(PacketType.Play.Server.NAMED_ENTITY_SPAWN)) {
                    final UUID uniqueId = packet.getUUIDs().read(0);
                    final Player player = Bukkit.getPlayer(uniqueId);

                    if (manager.isVanished(player) && !receiver.hasPermission("essentials.vanish")) {
                        event.setCancelled(true);
                    }

                    return;
                }

                if (packet.getType().equals(PacketType.Play.Server.ENTITY_DESTROY)) {
                    final int[] entityIds = packet.getIntegerArrays().read(0);
                    final List<Integer> entityIdList = Lists.newArrayList();

                    for (int id : entityIds) {
                        final Entity entity = manager.getEssentials().getOwner().getProtocolManager().getEntityFromID(receiver.getWorld(), id);

                        if (!(entity instanceof Player)) {
                            continue;
                        }

                        final Player player = (Player)entity;

                        if (player == null) {
                            entityIdList.add(id);
                            break;
                        }

                        if (!manager.isVanished(player)) {
                            entityIdList.add(id);
                            break;
                        }

                        if (manager.isVanished(player) && receiver.canSee(player)) {
                            entityIdList.add(id);
                            break;
                        }
                    }

                    if (entityIdList.size() >= 1) {
                        packet.getIntegerArrays().write(0, entityIdList.stream().mapToInt(i -> i).toArray());
                        return;
                    }

                    event.setCancelled(true);
                    return;
                }

                int entityId;

                if (packet.getType().equals(PacketType.Play.Server.COLLECT)) {
                    entityId = packet.getIntegers().read(1);
                } else {
                    entityId = packet.getIntegers().read(0);
                }

                final Entity entity = manager.getEssentials().getOwner().getProtocolManager().getEntityFromID(receiver.getWorld(), entityId);

                if (!(entity instanceof Player)) {
                    return;
                }

                final Player player = (Player)entity;

                if (player != null && manager.isVanished(player) && !receiver.canSee(player)) {
                    event.setCancelled(true);
                }
            }
        });

        // Listens for sound effects
        manager.getEssentials().getOwner().getProtocolManager().addPacketListener(new PacketAdapter(manager.getEssentials().getOwner(), ListenerPriority.HIGH, PacketType.Play.Server.NAMED_SOUND_EFFECT) {

            @Override
            public void onPacketSending(PacketEvent event) {
                final PacketContainer packet = event.getPacket();

                if (
                        packet != null &&
                        packet.getSoundCategories() != null &&
                        packet.getSoundCategories().read(0) != null &&
                        packet.getSoundCategories().read(0).equals(EnumWrappers.SoundCategory.PLAYERS)) {

                    final int x = packet.getIntegers().read(0) / 8;
                    final int y = packet.getIntegers().read(1) / 8;
                    final int z = packet.getIntegers().read(2) / 8;
                    final Player receiver = event.getPlayer();

                    if (receiver.hasPermission("essentials.vanish")) {
                        return;
                    }

                    manager.getVanished().forEach(uuid -> {
                        final Player player = Bukkit.getPlayer(uuid);

                        if (
                                player != null &&
                                !receiver.canSee(player) &&
                                receiver.getWorld().equals(player.getWorld()) &&
                                player.getLocation().distanceSquared(new Location(player.getWorld(), x, y, z)) < 2.0) {

                            event.setCancelled(true);

                        }
                    });
                }
            }

        });

        // Listens for and disables block dust
        manager.getEssentials().getOwner().getProtocolManager().addPacketListener(new PacketAdapter(manager.getEssentials().getOwner(), ListenerPriority.HIGH, PacketType.Play.Server.WORLD_PARTICLES) {

            @Override
            public void onPacketSending(PacketEvent event) {
                final PacketContainer packet = event.getPacket();

                if (!packet.getParticles().read(0).equals(EnumWrappers.Particle.BLOCK_DUST)) {
                    return;
                }

                final float x = event.getPacket().getFloat().read(0);
                final float y = event.getPacket().getFloat().read(1);
                final float z = event.getPacket().getFloat().read(2);
                final Player receiver = event.getPlayer();

                manager.getVanished().forEach(uuid -> {
                    final Player player = Bukkit.getPlayer(uuid);

                    if (
                            player != null &&
                            !receiver.canSee(player) &&
                            receiver.getWorld().equals(player.getWorld()) &&
                            player.getLocation().distanceSquared(new Location(player.getWorld(), x, y, z)) < 3.0) {

                        event.setCancelled(true);

                    }
                });
            }

        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // Player can't vanish, hide all players that are vanished from them
        if (!player.hasPermission("essentials.vanish")) {
            manager.getHandler().hideExisting(player);
        }

        // Player has a staff account and has toggle vanish on join enabled
        final StaffAccount staffAccount = manager.getEssentials().getStaffManager().getAccountByID(player.getUniqueId());

        if (staffAccount != null && staffAccount.isEnabled(StaffAccount.StaffSetting.JOIN_VANISHED)) {
            manager.getHandler().hidePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        manager.getHandler().showPlayer(player);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();
        final Action action = event.getAction();

        if (event.isCancelled()) {
            return;
        }

        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (block == null || !(block.getType().equals(Material.CHEST) || block.getType().equals(Material.TRAPPED_CHEST))) {
            return;
        }

        if (!manager.isVanished(player)) {
            return;
        }

        final BlockState state = block.getState();
        final Chest chest = (Chest)state;

        final ChestMenu menu = new ChestMenu(manager.getEssentials().getOwner(), player, chest.getInventory());
        menu.open();

        event.setCancelled(true);

        player.sendMessage(ChatColor.DARK_AQUA + "Silently opening chest...");
    }
}