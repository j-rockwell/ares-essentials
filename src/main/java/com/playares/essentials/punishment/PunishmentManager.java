package com.playares.essentials.punishment;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.playares.commons.connect.mongodb.MongoDB;
import com.playares.commons.logger.Logger;
import com.playares.commons.util.bukkit.Scheduler;
import com.playares.commons.util.general.IPS;
import com.playares.commons.util.general.Time;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.punishment.data.Punishment;
import com.playares.essentials.punishment.data.PunishmentType;
import com.playares.essentials.punishment.listener.PunishmentListener;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PunishmentManager {
    @Getter public final EssentialsService essentials;
    @Getter public final PunishmentHandler handler;

    public PunishmentManager(EssentialsService essentials) {
        this.essentials = essentials;
        this.handler = new PunishmentHandler(this);

        essentials.getOwner().registerListener(new PunishmentListener(this));
    }

    /**
     * Returns an Immutable Collection containing all active punishments for the provided Bukkit UUID and IP Address
     * @param uniqueId Bukkit UUID
     * @param address Converted IP Address
     * @return Immutable Collection of Punishments
     */
    public ImmutableCollection<Punishment> getActivePunishments(UUID uniqueId, long address) {
        final MongoDB database = (MongoDB)essentials.getOwner().getDatabaseInstance(MongoDB.class);
        final MongoCollection<Document> collection = database.getCollection(essentials.getDatabaseName(), "punishments");
        final MongoCursor<Document> cursor = collection.find(Filters.and(
                    Filters.or(Filters.eq("punished", uniqueId), Filters.eq("address", address)),

                    Filters.and(Filters.or(Filters.eq("expire", 0L), Filters.gt("expire", Time.now())),
                            Filters.eq("appealed", false))
                )
        ).cursor();

        final List<Punishment> result = Lists.newArrayList();

        while (cursor.hasNext()) {
            final Document document = cursor.next();
            result.add(new Punishment().fromDocument(document));
        }

        return ImmutableList.copyOf(result);
    }

    /**
     * Returns an Immutable Collection containing all active punishments of the given type for the provided Bukkit UUID and IP Address
     * @param uniqueId Bukkit UUID
     * @param address Converted IP Address
     * @param type Punishment Type
     * @return Immutable Collection of Punishments
     */
    public ImmutableCollection<Punishment> getActivePunishments(UUID uniqueId, long address, PunishmentType type) {
        final ImmutableCollection<Punishment> allPunishments = getActivePunishments(uniqueId, address);
        final List<Punishment> result = Lists.newArrayList();

        result.addAll(allPunishments.stream().filter(punishment -> punishment.getPunishmentType().equals(type)).collect(Collectors.toList()));

        return ImmutableList.copyOf(result);
    }

    /**
     * Handles saving punishments to the database
     * @param blocking Block the current thread
     * @param punishments Punishments to save
     */
    public void setPunishment(boolean blocking, Punishment... punishments) {
        if (blocking) {
            final MongoDB database = (MongoDB)essentials.getOwner().getDatabaseInstance(MongoDB.class);
            final MongoCollection<Document> collection = database.getCollection(essentials.getDatabaseName(), "punishments");

            for (Punishment punishment : punishments) {
                final Document existing = collection.find(Filters.eq("id")).first();

                if (existing == null) {
                    collection.insertOne(punishment.toDocument());
                } else {
                    collection.replaceOne(existing, punishment.toDocument());
                }
            }

            Logger.print("Finished saving " + punishments.length + " Punishments");

            return;
        }

        new Scheduler(essentials.getOwner()).async(() -> {

            final MongoDB database = (MongoDB)essentials.getOwner().getDatabaseInstance(MongoDB.class);
            final MongoCollection<Document> collection = database.getCollection(essentials.getDatabaseName(), "punishments");

            for (Punishment punishment : punishments) {
                final Document existing = collection.find(Filters.eq("id")).first();

                if (existing == null) {
                    collection.insertOne(punishment.toDocument());
                } else {
                    collection.replaceOne(existing, punishment.toDocument());
                }
            }

            new Scheduler(essentials.getOwner()).sync(() -> Logger.print("Finished saving " + punishments.length + " Punishments")).run();

        }).run();
    }

    /**
     * Returns a kick message for the provided Punishment
     * @param punishment Punishment
     * @return Kick message
     */
    public String getKickMessage(Punishment punishment) {
        final List<String> result = Lists.newArrayList();

        if (punishment.getPunishmentType().equals(PunishmentType.BLACKLIST)) {
            result.add(ChatColor.RED + "Your account has been blacklisted from Ares");
        }

        if (punishment.getPunishmentType().equals(PunishmentType.BAN)) {
            result.add(ChatColor.RED + "Your account has been banned from Ares");

            if (punishment.isForever()) {
                result.add(ChatColor.RED + "This punishment will " + ChatColor.RED + "" + ChatColor.UNDERLINE + "never" + ChatColor.RED + " expire");
            } else {
                result.add(ChatColor.RED + "This punishment will expire in " + Time.convertToRemaining(punishment.getExpireDate() - Time.now()));
            }
        }

        result.add(ChatColor.RED + "Appeal at https://playares.com/appeal");
        return Joiner.on(ChatColor.RESET + "\n").join(result);
    }

    /**
     * Returns a chat message for the provided mute punishment
     * @param punishment Punishment
     * @return Mute message
     */
    public String getMuteMessage(Punishment punishment) {
        List<String> result = Lists.newArrayList();

        if (punishment.isForever()) {
            result.add(ChatColor.RED + "You have been silenced for: " + punishment.getReason());
            result.add(ChatColor.RED + "This punishment will not expire");
        } else {
            result.add(ChatColor.RED + "You have been temporarily silenced for: " + punishment.getReason());
            result.add(ChatColor.RED + "This punishment will expire in " + Time.convertToRemaining(punishment.getExpireDate() - Time.now()));
        }

        result.add(ChatColor.RED + "Appeal at https://playares.com/appeal");
        return Joiner.on(ChatColor.RESET + "\n").join(result);
    }

    /**
     * Returns a List of players that have the same IP address as the provided player
     * @param player Player
     * @return List of Players
     */
    public List<Player> getMatchingPlayers(Player player) {
        if (player == null || player.getAddress() == null) {
            return Lists.newArrayList();
        }

        final long address = IPS.toLong(player.getAddress().getAddress().getHostAddress());

        return Bukkit.getOnlinePlayers().stream().filter(otherPlayer -> !otherPlayer.getUniqueId().equals(player.getUniqueId()) &&
                IPS.toLong(otherPlayer.getAddress().getAddress().getHostAddress()) == address).collect(Collectors.toList());
    }
}