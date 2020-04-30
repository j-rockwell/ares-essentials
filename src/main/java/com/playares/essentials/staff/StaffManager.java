package com.playares.essentials.staff;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.staff.data.StaffAccount;
import com.playares.essentials.staff.listener.StaffListener;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.playares.commons.connect.mongodb.MongoDB;
import com.playares.commons.logger.Logger;
import com.playares.commons.util.bukkit.Scheduler;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StaffManager {
    @Getter public final EssentialsService essentials;
    @Getter public final StaffHandler handler;
    @Getter public final Set<StaffAccount> staffRepository;

    public StaffManager(EssentialsService essentials) {
        this.essentials = essentials;
        this.handler = new StaffHandler(this);
        this.staffRepository = Sets.newConcurrentHashSet();

        essentials.getOwner().registerListener(new StaffListener(this));
    }

    /**
     * Returns a Staff Account matching the provided UUID
     * @param uniqueId Bukkit UUID
     * @return Staff Account
     */
    public StaffAccount getAccountByID(UUID uniqueId) {
        return staffRepository.stream().filter(staff -> staff.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    /**
     * Returns an Immutable Set containing all Staff Accounts with the provided permission set/unset
     * @param setting Staff Setting
     * @param value True/False
     * @return Immutable Set of Staff Accounts
     */
    public ImmutableSet<StaffAccount> getAccountByPermission(StaffAccount.StaffSetting setting, boolean value) {
        return ImmutableSet.copyOf(staffRepository.stream().filter(staff -> staff.getSettings().getSettings().getOrDefault(setting, setting.defaultSetting) == value && staff.isVerified()).collect(Collectors.toSet()));
    }

    /**
     * Returns a Staff Account from the database
     * @param filter Bson Filter
     * @return Staff Account
     */
    public StaffAccount getAccountFromDatabase(Bson filter) {
        final MongoDB database = (MongoDB)essentials.getOwner().getDatabaseInstance(MongoDB.class);
        final MongoCollection<Document> collection = database.getCollection(essentials.getDatabaseName(), "staff_accounts");
        final Document document = collection.find(filter).first();

        if (document == null) {
            return null;
        }

        return new StaffAccount().fromDocument(document);
    }

    /**
     * Saves the provided accounts to the database
     * @param blocking Block the main thread
     * @param accounts Staff Accounts
     */
    public void setAccount(boolean blocking, StaffAccount... accounts) {
        if (blocking) {
            for (StaffAccount account : accounts) {
                final MongoDB database = (MongoDB)essentials.getOwner().getDatabaseInstance(MongoDB.class);
                final MongoCollection<Document> collection = database.getCollection(essentials.getDatabaseName(), "staff_accounts");
                final Document existing = collection.find(Filters.eq("id", account.getUniqueId())).first();

                if (existing == null) {
                    collection.insertOne(account.toDocument());
                } else {
                    collection.replaceOne(existing, account.toDocument());
                }
            }

            Logger.print("Finished saving " + accounts.length + " Staff Accounts");
            return;
        }

        new Scheduler(essentials.getOwner()).async(() -> {

            for (StaffAccount account : accounts) {
                final MongoDB database = (MongoDB)essentials.getOwner().getDatabaseInstance(MongoDB.class);
                final MongoCollection<Document> collection = database.getCollection(essentials.getDatabaseName(), "staff_accounts");
                final Document existing = collection.find(Filters.eq("id", account.getUniqueId())).first();

                if (existing == null) {
                    collection.insertOne(account.toDocument());
                } else {
                    collection.replaceOne(existing, account.toDocument());
                }
            }

        }).run();
    }

    /**
     * Handles deleting the provided Staff Account from the database
     * @param account Staff Account
     */
    public void deleteAccountFromDatabase(StaffAccount account) {
        new Scheduler(essentials.getOwner()).async(() -> {

            final MongoDB database = (MongoDB)essentials.getOwner().getDatabaseInstance(MongoDB.class);
            final MongoCollection<Document> collection = database.getCollection(essentials.getDatabaseName(), "staff_accounts");
            final Document existing = collection.find(Filters.eq("id", account.getUniqueId())).first();

            if (existing == null) {
                return;
            }

            collection.deleteOne(existing);

            new Scheduler(essentials.getOwner()).sync(() -> Logger.print("Deleted Staff Account " + account.getUniqueId().toString())).run();

        }).run();
    }
}