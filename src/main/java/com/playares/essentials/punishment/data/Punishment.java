package com.playares.essentials.punishment.data;

import com.playares.commons.connect.mongodb.MongoDocument;
import com.playares.commons.util.general.Time;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.UUID;

public final class Punishment implements MongoDocument<Punishment> {
    @Getter public UUID uniqueId;
    @Getter public PunishmentType punishmentType;
    @Getter public UUID punishedId;
    @Getter public UUID creatorId;
    @Getter public long punishedAddress;
    @Getter public String reason;
    @Getter public long createDate;
    @Getter public long expireDate;
    @Getter @Setter public boolean forever;
    @Getter @Setter public boolean appealed;

    public Punishment() {
        this.uniqueId = null;
        this.punishmentType = null;
        this.punishedId = null;
        this.creatorId = null;
        this.punishedAddress = 0L;
        this.reason = null;
        this.createDate = 0L;
        this.expireDate = 0L;
        this.forever = false;
        this.appealed = false;
    }

    public Punishment(PunishmentType type, UUID punishedId, UUID creatorId, long punishedAddress, String reason) {
        this.uniqueId = UUID.randomUUID();
        this.punishmentType = type;
        this.punishedId = punishedId;
        this.creatorId = creatorId;
        this.punishedAddress = punishedAddress;
        this.reason = reason;
        this.createDate = Time.now();
        this.expireDate = 0L;
        this.forever = true;
        this.appealed = false;
    }

    public Punishment(PunishmentType type, UUID punishedId, UUID creatorId, long punishedAddress, String reason, long expireDate) {
        this.uniqueId = UUID.randomUUID();
        this.punishmentType = type;
        this.punishedId = punishedId;
        this.creatorId = creatorId;
        this.punishedAddress = punishedAddress;
        this.reason = reason;
        this.createDate = Time.now();
        this.expireDate = expireDate;
        this.forever = false;
        this.appealed = false;
    }

    /**
     * Returns true if this punishment is expired
     * @return True if expired
     */
    public boolean isExpired() {
        return (Time.now() >= expireDate) || appealed;
    }

    @Override
    public Punishment fromDocument(Document document) {
        this.uniqueId = (UUID)document.get("id");
        this.punishmentType = PunishmentType.valueOf(document.getString("type"));
        this.punishedId = (UUID)document.get("punished_id");
        this.creatorId = (UUID)document.get("creator_id");
        this.punishedAddress = document.getLong("punished_address");
        this.reason = document.getString("reason");
        this.createDate = document.getLong("create_date");
        this.expireDate = document.getLong("expire_date");
        this.forever = document.getBoolean("forever");
        this.appealed = document.getBoolean("appealed");

        return this;
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("id", uniqueId)
                .append("type", punishmentType.name())
                .append("punished_id", punishedId)
                .append("creator_id", creatorId)
                .append("punished_address", punishedAddress)
                .append("reason", reason)
                .append("create_date", createDate)
                .append("expire_date", expireDate)
                .append("forever", forever)
                .append("appealed", appealed);
    }
}