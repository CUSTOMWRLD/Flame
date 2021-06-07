package com.customwrld.flame.util;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Config;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.rank.Rank;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class Placeholders extends PlaceholderExpansion {
    private final String author;
    private final String identifier;
    private final String version;

    public Placeholders(String author, String identifier, String version) {
        this.author = author;
        this.identifier = identifier;
        this.version = version;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        Rank rank = Profile.getByUuid(player.getUniqueId()).getActiveRank();
        return switch (identifier) {
            case ("format") -> FlameAPI.getChatFormat(Profile.getByUuid(player.getUniqueId()));
            case ("name") -> FlameAPI.getColoredName(Profile.getByUuid(player.getUniqueId()));
            case ("prefix") -> FlameAPI.getColoredPrefix(Profile.getByUuid(player.getUniqueId()));
            case ("rank") -> rank.getColor() + rank.getName();
            case ("server") -> Config.SERVER_NAME.format();
            case ("weight") -> String.valueOf(rank.getWeight());
            default -> null;
        };
    }
}