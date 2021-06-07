package com.customwrld.flame.util.balance;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CollectionsUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class Transaction {

    private static final MongoCollection<Document> collection = CustomLib.getCustomLib().getMongoDatabase().getCollection("transactions");

    @Nullable private ObjectId id;
    private final UUID owner;

    private final List<TransactionEntry> entries;
    private final Map<String, String> data;

    private final long timestamp;

    public Transaction(@Nullable ObjectId id, UUID owner, List<TransactionEntry> entries, long timestamp, Map<String, String> data) {
        this.owner = owner;

        this.entries = entries;
        this.data = data;

        this.timestamp = timestamp;
    }

    public Transaction(@Nullable ObjectId id, UUID owner, List<TransactionEntry> entries, long timestamp) {
        this(id, owner, entries, timestamp, new HashMap<>());
    }

    public static Transaction load(@NotNull Document document) {
        List<TransactionEntry> entries = new ArrayList<>();
        for (Map<?, ?> obj : document.getList("entries", Map.class, new ArrayList<>())) {
            entries.add(TransactionEntry.fromSimple(
                    CollectionsUtil.genericMapToType(obj, String.class, Object.class)));
        }

        Map<String, String> data;
        Object obj = document.get("data");
        if (obj instanceof HashMap) {
            data = CollectionsUtil.genericMapToType((HashMap<?, ?>) document.get("data"), String.class, String.class);
        } else {
            data = new HashMap<>();
        }

        return new Transaction(
                document.getObjectId("_id"),
                UUID.fromString(document.getString("owner")),
                entries,
                document.getLong("timestamp"),
                data
        );
    }

    public static List<Transaction> getPlayerTransactions(UUID playerUUID) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (Document document : collection.find(Filters.eq("owner", playerUUID.toString()))) {
            transactions.add(load(document));
        }
        return transactions;
    }

    public static List<Transaction> getWithQuery(Bson query) {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (Document document : collection.find(query)) {
            transactions.add(load(document));
        }
        return transactions;
    }

    public void save() {
        Document document = new Document();
        if (id != null) {
            document.put("_id", id);
        }
        document.put("owner", owner.toString());

        List<HashMap<String, Object>> simpleEntries = new ArrayList<>();

        for (TransactionEntry entry : entries) {
            simpleEntries.add(entry.simplify());
        }

        document.put("entries", simpleEntries);
        document.put("data", data);
        document.put("timestamp", timestamp);

        if (id == null) {
            collection.insertOne(document);
        } else {
            collection.replaceOne(Filters.eq("_id", id.toString()), document, new ReplaceOptions().upsert(true));
        }
    }
}
