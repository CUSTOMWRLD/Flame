package com.customwrld.flame.design;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.customlib.util.MC;
import com.customwrld.customlib.util.NMSUtil;
import com.customwrld.flame.FlameAPI;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Tab {

    @Getter private static final Map<UUID, PacketPlayOutScoreboardTeam> teamMap = new HashMap<>();

    private static final TextComponent EXTENDED_SEPARATOR = Component.text()
            .append(Component.text("    "))
            .append(Component.text(StringUtils.repeat(" ", 65),
                    MC.CC.DARK_GRAY.getTextColor(),
                    TextDecoration.STRIKETHROUGH))
            .append(Component.text("    ")).build();

    public static String getTeamName(Profile profile) {
        Rank rank = profile.api().getActiveGrant().api().getRank();

        String trimmedName = StringUtils.left(profile.getName(), 13);

        if(rank != null) {
            return String.format("%03d", rank.getWeight()) + trimmedName;
        }

        return "000" + trimmedName;
    }

    public static void updateTab(Player player) {
        List<Component> header = new ArrayList<>();
        header.add(EXTENDED_SEPARATOR);
        header.add(Component.text("CUSTOMWRLD Network", MC.CC.BRAND.getTextColor(), TextDecoration.BOLD));
        header.add(Component.text("CUSTOMWRLD.COM", MC.CC.BRAND_LIGHT.getTextColor()));
        header.add(EXTENDED_SEPARATOR);

        List<Component> footer = new ArrayList<>();
        footer.add(EXTENDED_SEPARATOR);
        footer.add(Component.text()
                .append(Component.text("STORE ", MC.CC.BRAND_LIGHT.getTextColor(), TextDecoration.BOLD))
                .append(Component.text("store.customwrld.com"))
                .build());
        footer.add(Component.text()
                .append(Component.text("DISCORD ", MC.CC.BRAND_LIGHT.getTextColor(), TextDecoration.BOLD))
                .append(Component.text("discord.customwrld.com"))
                .build());
        footer.add(EXTENDED_SEPARATOR);

        player.sendPlayerListHeaderAndFooter(Component.join(Component.newline(), header), Component.join(Component.newline(), footer));
    }

    public static void updateTeam(Player player) {
        System.out.println("Updating " + player.getName() + "'s team!");
        deleteTeam(player);
        createTeam(player);
    }

    public static void createTeam(Player player) {
        Profile profile = Profile.getProfile(player.getUniqueId());

        if(profile != null) {
            Rank rank = profile.api().getActiveGrant().api().getRank();

            PacketPlayOutScoreboardTeam scoreboardTeam = new PacketPlayOutScoreboardTeam();

            try {
                NMSUtil.setValue(scoreboardTeam, "a", getTeamName(profile));
                NMSUtil.setValue(scoreboardTeam, "c", new ChatComponentText(MC.Style.toLegacy(FlameAPI.getColoredPrefix(profile))));
                NMSUtil.setValue(scoreboardTeam, "g", EnumChatFormat.valueOf(rank.getColor()));
                NMSUtil.setValue(scoreboardTeam, "h", Collections.singletonList(player.getName()));
                NMSUtil.setValue(scoreboardTeam, "i", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            teamMap.put(player.getUniqueId(), scoreboardTeam);

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> NMSUtil.sendPacket(onlinePlayer, scoreboardTeam));
        }
    }

    public static void deleteTeam(Player player) {
        Profile profile = Profile.getProfile(player.getUniqueId());

        if(profile != null) {
            PacketPlayOutScoreboardTeam scoreboardTeam = teamMap.get(player.getUniqueId());

            try {
                NMSUtil.setValue(scoreboardTeam, "i", 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            teamMap.remove(player.getUniqueId());

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> NMSUtil.sendPacket(onlinePlayer, scoreboardTeam));
        }
    }

}
