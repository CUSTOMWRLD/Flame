package com.customwrld.flame.util.balance;

import com.customwrld.customlib.util.CollectionsUtil;
import com.customwrld.flame.util.Currencies;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class TransactionEntry {
    private final String context;
    private final Map<Currencies.CurrencyType, Integer> amounts;

    public static TransactionEntry fromSimple(Map<String, Object> simple) {
        return new TransactionEntry(
                String.valueOf(simple.get("context")),
                CollectionsUtil.genericMapToType((Map<?, ?>) simple.get("amounts"), Currencies.CurrencyType.class, Integer.class));
    }

    public HashMap<String, Object> simplify() {
        HashMap<String, Object> simple = new HashMap<>();
        simple.put("context", context);

        Map<String, String> simpleAmounts = amounts.entrySet().stream()
                .collect(Collectors.<Map.Entry<Currencies.CurrencyType, Integer>, String, String>toMap(
                        ent -> ent.getKey().toString(),
                        ent -> ent.getValue().toString()));
        simple.put("amounts", simpleAmounts);
        return simple;
    }
}