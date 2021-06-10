package com.customwrld.flame.util.balance;

import com.customwrld.customlib.util.ChatUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Currencies;
import com.customwrld.flame.util.Levels;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

public class Balance {

    public static boolean canAffordWithGold(Profile profile, int goldCost) {
        return profile.getGold() > goldCost;
    }

    public static void addGold(Profile profile, int amount) {
        profile.setGold(profile.getGold() + amount);
    }

    public static TextComponent getCannotAffordGoldMessage() {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("You don't have enough gold to purchase this item.").color(ChatColor.RED)
        );
    }

    public static TextComponent getRequiredGoldMessage(int required) {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("This item costs ").color(ChatColor.RED)
                        .append(Currencies.CurrencyType.GOLD.getFormat().apply(new TextComponent(required + Currencies.CurrencyType.GOLD.getSuffix())))
                        .append(".").reset().color(ChatColor.RED)
        );
    }

    public static int getGameModeGold(Profile profile, @Nullable String serverType) {
        return profile.getGameModeGold().getOrDefault(
                serverType == null ?
                        Flame.getFlame().getMainConfig().getString("SERVER.TYPE") :
                        serverType, 0);
    }

    public static boolean canAffordWithGameModeGold(Profile profile, @Nullable String serverType, int gmGoldCost) {
        return profile.getGameModeGold().getOrDefault(
                serverType == null ?
                        Flame.getFlame().getMainConfig().getString("SERVER.TYPE") :
                        serverType, 0) > gmGoldCost;
    }

    public static void addGameModeGold(Profile profile, @Nullable String serverType, int amount) {
        serverType = serverType == null ? Flame.getFlame().getMainConfig().getString("SERVER.TYPE") : serverType;
        int gmGold = profile.getGameModeGold().getOrDefault(serverType, 0);
        profile.getGameModeGold().put(serverType, gmGold + amount);
    }

    public static TextComponent getCannotAffordGameModeGoldMessage() {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("You don't have enough gamemode gold to purchase this item.").color(ChatColor.RED)
        );
    }

    public static TextComponent getRequiredGameModeGoldMessage(int required) {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("This item costs ").color(ChatColor.RED)
                        .append(Currencies.CurrencyType.GMGOLD.getFormat().apply(new TextComponent(required + Currencies.CurrencyType.GMGOLD.getSuffix())))
        );
    }

    public static boolean hasEnoughExperience(Profile profile, long experienceRequired) {
        return profile.getExperience() > experienceRequired;
    }

    public static TextComponent getNotEnoughExperienceMessage() {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("You don't have enough experience to purchase this item.").color(ChatColor.RED)
        );
    }

    public static TextComponent getNotEnoughLevelsMessage() {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("Your level is too low to purchase this item.").color(ChatColor.RED)
        );
    }

    public static TextComponent getRequiredExperienceMessage(long required) {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("This item requires at least ").color(ChatColor.RED)
                        .append(Levels.EXP_FORMAT.apply(new TextComponent(required + Levels.EXP_SUFFIX)))
        );
    }

    public static TextComponent getRequiredLevelMessage(int required) {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("You need at least ").color(ChatColor.RED)
                        .append(Levels.EXP_FORMAT.apply(new TextComponent(required + " " + Levels.getLevelAffix(required))))
                        .append(" to be able to purchase this item").reset().color(ChatColor.RED)
        );
    }

    public static TextComponent getCannotAffordMultiMessage() {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("You don't have enough gold or gamemode gold to purchase this item.").color(ChatColor.RED)
        );
    }

    public static TextComponent getRequiredMultiMessage(int requiredGold, int requiredGmGold) {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("This item costs ").color(ChatColor.RED)
                        .append(Currencies.CurrencyType.GOLD.getFormat().apply(new TextComponent(requiredGold + Currencies.CurrencyType.GOLD.getSuffix())))
                        .append(" or ").reset().color(ChatColor.RED)
                        .append(Currencies.CurrencyType.GMGOLD.getFormat().apply(new TextComponent(requiredGmGold + Currencies.CurrencyType.GMGOLD.getSuffix())))
        );
    }
}
