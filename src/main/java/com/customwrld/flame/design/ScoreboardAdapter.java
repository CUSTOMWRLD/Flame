package com.customwrld.flame.design;

import com.customwrld.commonlib.modules.balance.Levels;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.assemble.AssembleAdapter;
import com.customwrld.customlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScoreboardAdapter implements AssembleAdapter {

    @Override
    public Component getTitle(Player player) {
        return Component.text()
                .append(Component.text("CUSTOMWRLD", MC.CC.BRAND.getTextColor(), TextDecoration.BOLD))
                .append(Component.text(" | ", MC.CC.DARK_GRAY.getTextColor()))
                .append(Component.text("Hub", MC.CC.BRAND_LIGHT.getTextColor()))
                .build();
    }

    @Override
    public List<Component> getLines(Player player) {
        final List<Component> toReturn = new ArrayList<>();

        try {
            var profile = ProfileUtil.getAssociatedProfile(player).get();

            Rank rank = profile.api().getActiveGrant().api().getRank();
            int level = Levels.getLevelFromExperience(profile.getExperience());

            toReturn.add(MC.Style.SCOREBOARD_SEPARATOR);
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E ", MC.CC.BRAND.getTextColor()))
                    .append(Component.text("Online Players", MC.CC.BRAND_LIGHT.getTextColor(), TextDecoration.BOLD))
                    .build());
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E ", MC.CC.BRAND.getTextColor()))
                    .append(Component.text("0 / 1000", MC.CC.WHITE.getTextColor()))
                    .build());
            toReturn.add(Component.empty());
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E ", MC.CC.BRAND.getTextColor()))
                    .append(Component.text("Active Rank", MC.CC.BRAND_LIGHT.getTextColor(), TextDecoration.BOLD))
                    .build());
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E ", MC.CC.BRAND.getTextColor()))
                    .append(Component.text(rank.getName(), MC.CC.valueOf(rank.getColor()).getTextColor()))
                    .build());
            toReturn.add(Component.empty());
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E ", MC.CC.BRAND.getTextColor()))
                    .append(Component.text("Gold", MC.CC.BRAND_LIGHT.getTextColor(), TextDecoration.BOLD))
                    .build());
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E ", MC.CC.BRAND.getTextColor()))
                    .append(Component.text(profile.getGold() + "G", MC.CC.YELLOW.getTextColor()))
                    .build());
            toReturn.add(Component.empty());
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E ", MC.CC.BRAND.getTextColor()))
                    .append(Component.text("Level", MC.CC.BRAND_LIGHT.getTextColor(), TextDecoration.BOLD))
                    .build());
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E ", MC.CC.BRAND.getTextColor()))
                    .append(Component.text(level, TextColor.color(Levels.getLevelColor(level).getRGB())))
                    .build());
            toReturn.add(Component.empty());
            toReturn.add(Component.text()
                    .append(Component.text(" \u258E customwrld.com", MC.CC.BRAND.getTextColor()))
                    .build());
            toReturn.add(MC.Style.SCOREBOARD_SEPARATOR);

            return toReturn;
        } catch(InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

}
