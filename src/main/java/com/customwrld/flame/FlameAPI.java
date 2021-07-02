package com.customwrld.flame;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.MC;
import com.customwrld.flame.design.Tab;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import com.customwrld.flame.util.Util;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FlameAPI {

    @Getter @Setter private static boolean chatMuted = false;
    @Getter @Setter private static boolean startingUp = true;
    @Getter @Setter private static boolean shuttingDown = false;

    public static boolean canStartProcedure(Player player) {
        return GrantProcedure.getByPlayer(player) == null && PunishmentProcedure.getByPlayer(player) == null;
    }

    public static void refreshPlayers(UUID rank) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getProfile(player.getUniqueId());

            if(profile != null) {
                if(profile.api().getActiveGrant().getRankUUID().equals(rank)) {
                    profile.api().getActiveGrant();

                    Tab.updateTeam(player);
                }
            }
        }
    }

    public static Component getChatFormat(Profile profile) {
        return getColoredPrefix(profile).append(getColoredName(profile));
    }

    public static Component getColoredName(Profile profile) {
        Rank rank = profile.api().getActiveGrant().api().getRank();
        return Component.text(profile.getName()).color(TextColor.color(rank.api().getRankColor().getRGB()));
    }

    public static Component getColoredPrefix(Profile profile) {
        Rank rank = profile.api().getActiveGrant().api().getRank();
        return rank.getPrefix() != null ? MC.Style.fromLegacy(rank.getPrefix()) : Component.empty();
    }

}
