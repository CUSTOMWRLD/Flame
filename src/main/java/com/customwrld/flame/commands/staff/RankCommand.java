package com.customwrld.flame.commands.staff;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.menus.general.RankListMenu;
import com.customwrld.flame.pigeon.payloads.RankUpdatePayload;
import com.customwrld.flame.rank.Rank;
import com.customwrld.flame.rank.UpdateType;
import com.customwrld.flame.util.Arguments;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ChatColorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.customwrld.flame.util.Arguments.rankArgument;

public class RankCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("rank")
                .withPermission("flame.rank")
                .withSubcommand(new CommandAPICommand("perm")
                        .withSubcommand(permAdd())
                        .withSubcommand(permRemove()))
                .withSubcommand(new CommandAPICommand("inherit")
                        .withSubcommand(inheritAdd())
                        .withSubcommand(inheritRemove()))
                .withSubcommand(setColor())
                .withSubcommand(list())
                .withSubcommand(setWeight())
                .withSubcommand(create())
                .withSubcommand(delete())
                .withSubcommand(setPrefix());
    }

    public static CommandAPICommand permAdd() {
        return new CommandAPICommand("add")
                .withPermission("flame.rank.perm.add")
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(new StringArgument("permission"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    String permission = (String) args[1];

                    if (rank == null) {
                        sender.sendMessage(CC.RED + Config.RANK_NOT_FOUND.format());
                        return;
                    }

                    if (rank.getPermissions().contains(permission)) {
                        sender.sendMessage(CC.RED + "That rank is already assigned that permission.");
                        return;
                    }

                    for (Rank childRank : rank.getInherited()) {
                        if (childRank.hasPermission(permission)) {
                            sender.sendMessage(CC.RED + "That rank is inheriting that permission from the " + rank.getColor() + rank.getName() + " rank.");
                            return;
                        }
                    }

                    rank.getPermissions().add(permission);
                    rank.save();

                    new RankUpdatePayload(UpdateType.PERMISSION_ADDED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    public static CommandAPICommand permRemove() {
        return new CommandAPICommand("remove")
                .withPermission("flame.rank.perm.remove")
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(new StringArgument("permission"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    String permission = (String) args[1];

                    if (rank == null) {
                        sender.sendMessage(CC.RED + Config.RANK_NOT_FOUND.format());
                        return;
                    }

                    if (!rank.getPermissions().contains(permission)) {
                        sender.sendMessage(CC.RED + "That rank is not assigned that permission.");
                    } else {
                        rank.getPermissions().remove(permission);
                        rank.save();

                        new RankUpdatePayload(UpdateType.PERMISSION_REMOVED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                    }
                });
    }

    public static CommandAPICommand inheritAdd() {
        return new CommandAPICommand("add")
                .withPermission("flame.rank.inherit.add")
                .withArguments(Arguments.rankArgument("parent"))
                .withArguments(Arguments.rankArgument("child"))
                .executes((sender, args) -> {
                    Rank parent = (Rank) args[0];
                    Rank child = (Rank) args[1];

                    if (parent == null || child == null) {
                        sender.sendMessage(Config.RANK_NOT_FOUND.format());
                        return;
                    }

                    if (parent.canInherit(child)) {
                        parent.getInherited().add(child);
                        parent.save();

                        new RankUpdatePayload(UpdateType.INHERIT_ADDED, Rank.SERIALIZER.serialize(parent), parent.getUuid()).send();
                    } else {
                        sender.sendMessage(ChatColor.RED + "The parent rank cannot inherit permissions from the inherit child rank.");
                    }
                });
    }

    public static CommandAPICommand inheritRemove() {
        return new CommandAPICommand("remove")
                .withPermission("flame.rank.inherit.remove")
                .withArguments(Arguments.rankArgument("parent"))
                .withArguments(Arguments.rankArgument("child"))
                .executes((sender, args) -> {
                    Rank parent = (Rank) args[0];
                    Rank child = (Rank) args[1];


                    if (parent == null || child == null) {
                        sender.sendMessage(Config.RANK_NOT_FOUND.format());
                        return;
                    }

                    if (parent.getInherited().remove(child)) {
                        parent.save();

                        new RankUpdatePayload(UpdateType.INHERIT_REMOVED, Rank.SERIALIZER.serialize(parent), parent.getUuid()).send();
                    } else {
                        sender.sendMessage(ChatColor.RED + "The parent rank does not inherit the child rank.");
                    }
                });
    }

    public static CommandAPICommand setColor() {
        return new CommandAPICommand("setcolor")
                .withPermission("flame.rank.setcolor")
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(new ChatColorArgument("color").safeOverrideSuggestions((sender, prevArgs) -> CC.toLegacy(CC.MAP.values()).stream()
                        .filter(org.bukkit.ChatColor::isColor)
                        .toArray(org.bukkit.ChatColor[]::new)))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    net.md_5.bungee.api.ChatColor color = ((org.bukkit.ChatColor) args[1]).asBungee();

                    if (rank == null) {
                        sender.sendMessage(CC.RED + Config.RANK_NOT_FOUND.format());
                        return;
                    }

                    if (color == net.md_5.bungee.api.ChatColor.RESET) {
                        sender.sendMessage(CC.RED + "That color does not exist.");
                        return;
                    }

                    rank.setColor(color);
                    rank.save();

                    new RankUpdatePayload(UpdateType.COLOR_UPDATED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    public static CommandAPICommand list() {
        return new CommandAPICommand("list")
                .withPermission("flame.rank.list")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;

                    new RankListMenu(player);
                });
    }

    public static CommandAPICommand setWeight() {
        return new CommandAPICommand("setweight")
                .withPermission("flame.rank.setweight")
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(new IntegerArgument("weight"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    Integer weight = (Integer) args[1];

                    if (rank == null) {
                        sender.sendMessage(CC.RED + Config.RANK_NOT_FOUND.format());
                        return;
                    }

                    rank.setWeight(weight);
                    rank.save();

                    new RankUpdatePayload(UpdateType.WEIGHT_UPDATED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    public static CommandAPICommand create() {
        return new CommandAPICommand("create")
                .withPermission("flame.rank.create")
                .withArguments(new StringArgument("rank"))
                .executes((sender, args) -> {
                    String rankName = (String) args[0];

                    if (Rank.getRankByName(rankName) != null) {
                        sender.sendMessage(CC.RED + "A rank with that name already exists.");
                        return;
                    }

                    Rank rank = new Rank(rankName);
                    rank.save();

                    new RankUpdatePayload(UpdateType.CREATED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    public static CommandAPICommand delete() {
        return new CommandAPICommand("delete")
                .withPermission("flame.rank.delete")
                .withArguments(Arguments.rankArgument("rank"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];

                    if (rank == null) {
                        sender.sendMessage(CC.RED + "A rank with that name does not exist.");
                        return;
                    }

                    rank.delete();

                    new RankUpdatePayload(UpdateType.DELETED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    public static CommandAPICommand setPrefix() {
        return new CommandAPICommand("setprefix")
                .withPermission("flame.rank.setprefix")
                .withArguments(rankArgument("rank"))
                .withArguments(new StringArgument("prefix"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    String prefix = (String) args[1];

                    rank.setPrefix("\\u" + prefix);
                    rank.save();

                    new RankUpdatePayload(UpdateType.PREFIX_UPDATE, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }
}
