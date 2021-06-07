package com.customwrld.flame.commands.staff;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.menus.general.RankListMenu;
import com.customwrld.flame.pigeon.payloads.RankUpdatePayload;
import com.customwrld.flame.rank.Rank;
import com.customwrld.flame.rank.RankUpdateType;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ChatColorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static com.customwrld.flame.util.Arguments.rankArgument;

public class RankCommand extends Command {

    public RankCommand() {
        this.command = "rank";
        this.permission = "flame.rank";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
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

    private CommandAPICommand permAdd() {
        return new CommandAPICommand("add")
                .withPermission(this.permission)
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

                    new RankUpdatePayload(RankUpdateType.PERMISSION_ADDED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    private CommandAPICommand permRemove() {
        return new CommandAPICommand("remove")
                .withPermission(this.permission)
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

                        new RankUpdatePayload(RankUpdateType.PERMISSION_REMOVED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                    }
                });
    }

    private CommandAPICommand inheritAdd() {
        return new CommandAPICommand("add")
                .withPermission(this.permission)
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

                        new RankUpdatePayload(RankUpdateType.INHERIT_ADDED, Rank.SERIALIZER.serialize(parent), parent.getUuid()).send();
                    } else {
                        sender.sendMessage(ChatColor.RED + "The parent rank cannot inherit permissions from the inherit child rank.");
                    }
                });
    }

    private CommandAPICommand inheritRemove() {
        return new CommandAPICommand("remove")
                .withPermission(this.permission)
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

                        new RankUpdatePayload(RankUpdateType.INHERIT_REMOVED, Rank.SERIALIZER.serialize(parent), parent.getUuid()).send();
                    } else {
                        sender.sendMessage(ChatColor.RED + "The parent rank does not inherit the child rank.");
                    }
                });
    }

    private CommandAPICommand setColor() {
        return new CommandAPICommand("setcolor")
                .withPermission(this.permission)
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

                    new RankUpdatePayload(RankUpdateType.COLOR_UPDATED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    private CommandAPICommand list() {
        return new CommandAPICommand("list")
                .withPermission(this.permission)
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;

                    new RankListMenu(player);
                });
    }

    private CommandAPICommand setWeight() {
        return new CommandAPICommand("setweight")
                .withPermission(this.permission)
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

                    new RankUpdatePayload(RankUpdateType.WEIGHT_UPDATED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    private CommandAPICommand create() {
        return new CommandAPICommand("create")
                .withPermission(this.permission)
                .withArguments(new StringArgument("rank"))
                .executes((sender, args) -> {
                    String rankName = (String) args[0];

                    if (Rank.getRankByName(rankName) != null) {
                        sender.sendMessage(CC.RED + "A rank with that name already exists.");
                        return;
                    }

                    Rank rank = new Rank(rankName);
                    rank.save();

                    new RankUpdatePayload(RankUpdateType.CREATED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    private CommandAPICommand delete() {
        return new CommandAPICommand("delete")
                .withPermission(this.permission)
                .withArguments(Arguments.rankArgument("rank"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];

                    if (rank == null) {
                        sender.sendMessage(CC.RED + "A rank with that name does not exist.");
                        return;
                    }

                    rank.delete();

                    new RankUpdatePayload(RankUpdateType.DELETED, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }

    private CommandAPICommand setPrefix() {
        return new CommandAPICommand("setprefix")
                .withPermission(this.permission)
                .withArguments(rankArgument("rank"))
                .withArguments(new GreedyStringArgument("prefix"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    String prefix = (String) args[1];

                    rank.setPrefix(prefix);
                    rank.save();

                    new RankUpdatePayload(RankUpdateType.PREFIX_UPDATE, Rank.SERIALIZER.serialize(rank), rank.getUuid()).send();
                });
    }
}
