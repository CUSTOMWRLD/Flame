package com.customwrld.flame;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.flame.profile.grant.GrantProcedure;
import com.customwrld.flame.profile.punishment.PunishmentProcedure;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class FlameAPI {

    @Getter @Setter private static boolean chatMuted = false;
    @Getter @Setter private static boolean startingUp = true;
    @Getter @Setter private static boolean shuttingDown = false;

    public static boolean canStartProcedure(Player player) {
        return GrantProcedure.getByPlayer(player) == null && PunishmentProcedure.getByPlayer(player) == null;
    }

    public static TextComponent getChatFormat(Profile profile) {
        return getColoredPrefix(profile).append(getColoredName(profile));
    }

    public static TextComponent getColoredName(Profile profile) {
        Rank rank = profile.getApi().getActiveGrant().getApi().getRank();
        return Component.text(profile.getName()).color(TextColor.color(rank.getColor().getRGB()));
    }

    public static TextComponent getColoredPrefix(Profile profile) {
        Rank rank = profile.getApi().getActiveGrant().getApi().getRank();
        return rank.getPrefix() != null ? Minecraft.Style.fromLegacy(rank.getPrefix()) : Component.empty();
    }

}
