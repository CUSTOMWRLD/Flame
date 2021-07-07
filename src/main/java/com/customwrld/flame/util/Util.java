package com.customwrld.flame.util;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import com.customwrld.commonlib.util.MC;
import com.customwrld.commonlib.util.TimeUtil;
import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.flame.Flame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Util {

    public static final ItemStack NEXT_PAGE = new ItemFactory(Material.GRAY_CARPET)
            .setName(Component.text().append(Component.text("Next Page ")
                    .color(NamedTextColor.GRAY))
                    .append(Component.text(">")
                            .color(NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.BOLD, true))
                    .build())
            .build();
    public static final ItemStack PREVIOUS_PAGE = new ItemFactory(Material.GRAY_CARPET)
            .setName(Component.text().append(Component.text("< ")
                    .color(NamedTextColor.DARK_GRAY)
                    .decoration(TextDecoration.BOLD, true))
                    .append(Component.text("Previous Page")
                            .color(NamedTextColor.GRAY))
                    .build())
            .build();
    public static final ItemStack FILLER = new ItemFactory(Material.GRAY_STAINED_GLASS_PANE)
            .setName(Component.empty()).build();

    public static TextColor getPunishmentColor(Punishment punishment) {
        switch (punishment.getType()) {
            case BLACKLIST -> {
                return MC.CC.RED.getTextColor();
            }
            case BAN -> {
                return MC.CC.GOLD.getTextColor();
            }
            case MUTE -> {
                return MC.CC.GREEN.getTextColor();
            }
            case WARN -> {
                return MC.CC.BLUE.getTextColor();
            }
        }

        return MC.CC.WHITE.getTextColor();
    }

    public static List<Component> getPunishmentMessage(Punishment punishment) {
        Date date = new Date(punishment.getAddedAt());

        return Arrays.asList(
                MC.Style.SEPARATOR,
                Component.text("You are " + punishment.api().getContext() + (!punishment.api().isPermanent() ? " for " + punishment.api().getTimeRemaining() : "") + ".")
                        .color(NamedTextColor.RED),
                Component.empty(),
                Component.text("Reason: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(punishment.getAddedReason())
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.text("Added On: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(TimeUtil.dateToString(date, true))
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.text("Punishment ID: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(punishment.getId())
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.empty(),
                Component.text("Appeal At: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text("https://customwrld.com/discord")
                                        .color(NamedTextColor.AQUA)
                                        .decoration(TextDecoration.UNDERLINED, true)
                        ),
                MC.Style.SEPARATOR
        );
    }

    public static boolean isElevatedStaff(Player initiator, UUID player) {
        if (!Flame.getFlame().getElevatedStaff().contains(player)) {
            return false;
        } else {
            initiator.sendMessage(Component.text("You cannot use that command in this context.")
                    .color(MC.CC.RED.getTextColor()));
            return true;
        }
    }

    public static void addNavigation(Player player, SmartInventory inventory, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        contents.set(0, 0, ClickableItem.of(PREVIOUS_PAGE,
                e -> inventory.open(player, pagination.previous().getPage())));

        contents.set(0, 8, ClickableItem.of(NEXT_PAGE,
                e -> inventory.open(player, pagination.next().getPage())));
    }

    public static void setupBukkitPlayer(Profile profile) {
        Player player = Bukkit.getPlayer(profile.getId());

        Plugin plugin = Flame.getFlame();

        if (player != null) {
            player.setOp(profile.getStaffProfile().isOperator());

            for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
                if (attachmentInfo.getAttachment() == null || !attachmentInfo.getAttachment().getPlugin().equals(plugin)) {
                    continue;
                }

                attachmentInfo.getAttachment().getPermissions().forEach((permission, value) -> attachmentInfo.getAttachment().unsetPermission(permission));
            }

            PermissionAttachment attachment = player.addAttachment(plugin);

            for (String permission : profile.api().getAllPermissions()) {
                attachment.setPermission(permission, true);
            }

            player.recalculatePermissions();
        }
    }

}
