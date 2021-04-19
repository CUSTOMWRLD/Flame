package com.customwrld.flame;

import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.procedure.GrantProcedure;
import com.customwrld.flame.profile.punishment.procedure.PunishmentProcedure;
import com.customwrld.flame.rank.Rank;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class FlameAPI {

    @Getter private static ArrayList<UUID> frozenPlayers = new ArrayList<>();
    @Getter @Setter private static boolean serverFrozen = false;

    public static boolean canStartProcedure(Player player) {
        return GrantProcedure.getByPlayer(player) == null && PunishmentProcedure.getByPlayer(player) == null;
    }

    public static String getChatFormat(Profile profile) {
        return getColoredPrefix(profile) + " \uF806" + getColoredName(profile);
    }

    public static String getColoredName(Profile profile) {
        Rank rank = profile.getActiveRank();
        return rank.getColor() + profile.getName();
    }

    public static String getColoredPrefix(Profile profile) {
        Rank rank = profile.getActiveRank();
        String prefix = rank.getPrefix();

        if(prefix.equals("null")) {
            return "";
        } else {
            String str = prefix.split(" ")[0];
            str = str.replace("\\","");
            String[] arr = str.split("u");
            StringBuilder charPrefix = new StringBuilder();
            for(int i = 1; i < arr.length; i++) {
                int hexVal = Integer.parseInt(arr[i], 16);
                charPrefix.append((char) hexVal);
            }
            return charPrefix.toString();
        }
    }

}
