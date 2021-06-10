package com.customwrld.flame.util.balance;

import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Currencies;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractTransactionContext {

    private final Player player;
    private final String gameMode;
    private final List<TransactionEntry> entries;
    private final Map<String, String> data;
    private long timestamp;

    public AbstractTransactionContext(Player player, String gameMode, List<TransactionEntry> entries) {
        this.player = player;
        this.gameMode = gameMode;
        this.entries = entries;

        this.data = new HashMap<>();
    }

    public Transaction generateTransaction() {
        return new Transaction(null, player.getUniqueId(), entries, timestamp, data);
    }

    public Map<Currencies.CurrencyType, Integer> getTotals() {
        Map<Currencies.CurrencyType, Integer> totals = new HashMap<>();
        for (TransactionEntry entry : entries) {
            for (Map.Entry<Currencies.CurrencyType, Integer> amountsEnt : entry.getAmounts().entrySet()) {
                totals.put(amountsEnt.getKey(), totals.getOrDefault(amountsEnt.getKey(), 0) + amountsEnt.getValue());
            }
        }
        return totals;
    }

    public boolean process(boolean needsEnoughBalance, boolean logTransaction) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Map<Currencies.CurrencyType, Integer> totals = getTotals();
        if (needsEnoughBalance) {
            for (Map.Entry<Currencies.CurrencyType, Integer> ent : totals.entrySet()) {
                if (!ent.getKey().canAfford(profile, gameMode, ent.getValue())) {
                    return false;
                }
            }
        }

        processPayment(profile);
        if (logTransaction) {
            generateTransaction().save();
        }

        return true;
    }

    private void processPayment(Profile profile) {
        for (TransactionEntry entry : entries) {
            for (Map.Entry<Currencies.CurrencyType, Integer> amountEnt : entry.getAmounts().entrySet()) {
                amountEnt.getKey().add(profile, gameMode, amountEnt.getValue());
            }
        }
    }
}
