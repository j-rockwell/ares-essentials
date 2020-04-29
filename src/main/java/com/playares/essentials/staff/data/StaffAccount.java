package com.playares.essentials.staff.data;

import com.google.common.collect.Maps;
import com.playares.commons.connect.mongodb.MongoDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;

public final class StaffAccount implements MongoDocument<StaffAccount> {
    @Getter public UUID uniqueId;
    @Getter @Setter public boolean verified;
    @Getter @Setter public String password;
    @Getter public StaffSettings settings;

    public StaffAccount() {
        this.uniqueId = null;
        this.verified = false;
        this.password = null;
        this.settings = null;
    }

    public StaffAccount(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.verified = false;
        this.password = null;
        this.settings = new StaffSettings();
    }

    /**
     * Returns true if this Staff Account has the provided setting enabled
     * @param setting Setting
     * @return True if enabled
     */
    public boolean isEnabled(StaffSetting setting) {
        return settings.getSettings().getOrDefault(setting, setting.defaultSetting);
    }

    /**
     * Returns true if the provided input is the password for this account
     * @param input Input
     * @return True if correct
     */
    public boolean isPassword(String input) {
        return input.equals(password);
    }

    @Override
    public StaffAccount fromDocument(Document document) {
        this.uniqueId = (UUID)document.get("id");
        this.password = document.getString("password");
        this.settings = new StaffSettings().fromDocument(document.get("settings", Document.class));

        return this;
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("id", uniqueId)
                .append("password", password)
                .append("settings", settings.toDocument());
    }

    public final class StaffSettings implements MongoDocument<StaffSettings> {
        @Getter public final Map<StaffSetting, Boolean> settings;

        public StaffSettings() {
            this.settings = Maps.newHashMap();
        }

        @Override
        public StaffSettings fromDocument(Document document) {
            for (StaffSetting key : StaffSetting.values()) {
                boolean value = key.defaultSetting;

                if (document.containsKey(key.name())) {
                    value = document.getBoolean(key.name());
                }

                settings.put(key, value);
            }

            return this;
        }

        @Override
        public Document toDocument() {
            final Document document = new Document();

            for (StaffSetting key : settings.keySet()) {
                final boolean value = settings.get(key);

                document.append(key.name(), value);
            }

            return document;
        }
    }

    @AllArgsConstructor
    public enum StaffSetting {
        SHOW_CONNECTION_NOTIFICATIONS("Show connect/disconnect messages", "See all join and leave messages", false),
        SHOW_GLOBAL_CHAT("Show all chat messages", "Show all messages being sent", false),
        ALL_MESSAGES_BROADCAST("Broadcasting messages", "Broadcast your messages to the whole server", false),
        SHOW_TICKET_NOTIFICATIONS("Show ticket notifications", "View ticket notifications", true),
        SHOW_ALT_NOTIFICATIONS("Show alt account notifications", "View alt account notifications", true),
        JOIN_VANISHED("Automatically join vanished", "Automatically vanish upon joining the server", false);

        @Getter public final String displayName;
        @Getter public final String description;
        @Getter public final boolean defaultSetting;
    }
}