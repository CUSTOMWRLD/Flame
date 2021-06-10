package com.customwrld.flame;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.profile.rank.Rank;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class FlameAPI {

    @Getter
    @Setter
    private static boolean chatMuted = false;
    @Getter
    @Setter
    private static boolean startingUp = true;
    @Getter
    @Setter
    private static boolean shuttingDown = false;

    public static boolean canStartProcedure(Player player) {
        return GrantProcedure.getByPlayer(player) == null && PunishmentProcedure.getByPlayer(player) == null;
    }

    public static String getChatFormat(Profile profile) {
        return getColoredPrefix(profile) + getColoredName(profile);
    }

    public static String getColoredName(Profile profile) {
        Rank rank = profile.getActiveRank();
        return rank.getColor() + profile.getName();
    }

    public static String getColoredPrefix(Profile profile) {
        Rank rank = profile.getActiveRank();
        return rank.getPrefix() != null ? CC.translate(rank.getPrefix()) : "";
    }

}
