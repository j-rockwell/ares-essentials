package com.llewkcor.ares.essentials.staff.data;

import com.llewkcor.ares.commons.connect.mongodb.MongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.UUID;

public final class StaffDAO {
    private static final String NAME = "essentials";
    private static final String COLL = "staff";

    /**
     * Returns a Staff Account matching the provided Bukkit UUID in the database
     * @param database Database
     * @param uniqueId UUID
     * @return StaffAccount
     */
    public static StaffAccount getAccount(MongoDB database, UUID uniqueId) {
        final MongoCollection<Document> collection = database.getCollection(NAME, COLL);
        final Document existing;

        if (collection == null) {
            return null;
        }

        existing = collection.find(Filters.eq("id", uniqueId)).first();

        if (existing != null) {
            return new StaffAccount().fromDocument(existing);
        } else {
            return null;
        }
    }

    /**
     * Handles saving a Staff Account to the database
     * @param database Database
     * @param account StaffAccount
     */
    public static void saveAccount(MongoDB database, StaffAccount account) {
        final MongoCollection<Document> collection = database.getCollection(NAME, COLL);
        final Document existing;

        if (collection == null) {
            return;
        }

        existing = collection.find(Filters.eq("id", account.getUniqueId())).first();

        if (existing != null) {
            collection.replaceOne(existing, account.toDocument());
        } else {
            collection.insertOne(account.toDocument());
        }
    }
}
