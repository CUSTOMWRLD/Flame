package com.customwrld.flame.util;

import com.customwrld.customlib.util.ComponentFormatting;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.balance.Balance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Nullable;

public class Currencies {
    public static final float CONVERSION_RATE = 0.2f;

    public static int convertGold(int gold) {
        return (int) Math.floor(gold * CONVERSION_RATE);
    }

    @AllArgsConstructor
    @Getter
    public enum CurrencyType {
        GOLD("gold", "G", "Gold", ComponentFormatting.builder().color(ChatColor.GOLD).build()),
        GMGOLD("gmgold", "GG", "Gamemode Gold", ComponentFormatting.builder().color(ChatColor.RED).build());

        private final String id;
        private final String suffix;
        private final String fullname;
        private final ComponentFormatting format;

        public boolean canAfford(Profile profile, @Nullable String serverType, int amount) {
            return switch (this) {
                case GOLD -> Balance.canAffordWithGold(profile, amount);
                case GMGOLD -> Balance.canAffordWithGamemodeGold(profile, serverType, amount);
            };
        }

        public void add(Profile profile, @Nullable String serverType, int amount) {
            switch (this) {
                case GOLD -> Balance.addGold(profile, amount);
                case GMGOLD -> Balance.addGamemodeGold(profile, serverType, amount);
            }
        }

        public void remove(Profile profile, @Nullable String serverType, int amount) {
            switch (this) {
                case GOLD -> Balance.addGold(profile, -amount);
                case GMGOLD -> Balance.addGamemodeGold(profile, serverType, -amount);
            }
        }
    }
}