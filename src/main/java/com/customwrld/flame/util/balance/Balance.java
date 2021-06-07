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

    //Affordability
    public static boolean canAffordWithGold(Profile profile, int goldCost) {
        return profile.getGold() > goldCost;
    }

    //Modifiability
    public static void addGold(Profile profile, int amount) {
        profile.setGold(profile.getGold() + amount);
    }

    //Messages
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

    //Retrieval
    public static int getGamemodeGold(Profile profile, @Nullable String serverType) {
        return profile.getGamemodeGold().getOrDefault(
                serverType == null ?
                        Flame.getFlame().getMainConfig().getString("SERVER.TYPE") :
                        serverType, 0);
    }

    //Affordability
    public static boolean canAffordWithGamemodeGold(Profile profile, @Nullable String serverType, int gmGoldCost) {
        return profile.getGamemodeGold().getOrDefault(
                serverType == null ?
                        Flame.getFlame().getMainConfig().getString("SERVER.TYPE") :
                        serverType, 0) > gmGoldCost;
    }

    //Modifiability
    public static void addGamemodeGold(Profile profile, @Nullable String serverType, int amount) {
        serverType = serverType == null ? Flame.getFlame().getMainConfig().getString("SERVER.TYPE") : serverType;
        int gmGold = profile.getGamemodeGold().getOrDefault(serverType, 0);
        profile.getGamemodeGold().put(serverType, gmGold + amount);
    }

    //Messages
    public static TextComponent getCannotAffordGamemodeGoldMessage() {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("You don't have enough gamemode gold to purchase this item.").color(ChatColor.RED)
        );
    }

    public static TextComponent getRequiredGamemodeGoldMessage(int required) {
        return ChatUtil.builderToComponent(
                new ComponentBuilder("This item costs ").color(ChatColor.RED)
                        .append(Currencies.CurrencyType.GMGOLD.getFormat().apply(new TextComponent(required + Currencies.CurrencyType.GMGOLD.getSuffix())))
        );
    }

    //Affordability
    public static boolean hasEnoughExperience(Profile profile, long experienceRequired) {
        return profile.getExperience() > experienceRequired;
    }

    //Messages
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

    //Messages
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
