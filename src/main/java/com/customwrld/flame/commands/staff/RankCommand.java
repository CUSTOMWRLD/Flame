package com.customwrld.flame.commands.staff;

import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.menus.general.RankListMenu;
import com.customwrld.flame.pigeon.payloads.rank.ReloadRankPayload;
import com.customwrld.flame.pigeon.payloads.rank.RemoveRankPayload;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.pigeon.Pigeon;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ChatColorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;

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
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("add")
                .withPermission(this.permission)
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(new StringArgument("permission"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    String permission = (String) args[1];

                    if (rank.getPermissions().contains(permission)) {
                        sender.sendMessage(Component.text("That rank is already assigned that permission.")
                                .color(NamedTextColor.RED));
                        return;
                    }

                    for (Rank childRank : rank.getInherited()) {
                        if (childRank.api().hasPermission(permission)) {
                            sender.sendMessage(Component.text("That rank is inheriting that permission from the " + rank.getName() + " rank.")
                                    .color(NamedTextColor.RED));
                            return;
                        }
                    }

                    rank.getPermissions().add(permission);
                    rank.save();

                    pigeon.broadcast(new ReloadRankPayload(rank));
                });
    }

    private CommandAPICommand permRemove() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("remove")
                .withPermission(this.permission)
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(new StringArgument("permission"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    String permission = (String) args[1];

                    if (!rank.getPermissions().contains(permission)) {
                        sender.sendMessage(Component.text("That rank is not assigned that permission.")
                                .color(NamedTextColor.RED));
                    } else {
                        rank.getPermissions().remove(permission);
                        rank.save();

                        pigeon.broadcast(new ReloadRankPayload(rank));
                    }
                });
    }

    private CommandAPICommand inheritAdd() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("add")
                .withPermission(this.permission)
                .withArguments(Arguments.rankArgument("parent"))
                .withArguments(Arguments.rankArgument("child"))
                .executes((sender, args) -> {
                    Rank parent = (Rank) args[0];
                    Rank child = (Rank) args[1];

                    if (parent.api().canInherit(child)) {
                        parent.getInherited().add(child);
                        parent.save();

                        pigeon.broadcast(new ReloadRankPayload(parent));
                    } else {
                        sender.sendMessage(Component.text("The parent rank cannot inherit permissions from the inherit child rank.")
                                .color(NamedTextColor.RED));
                    }
                });
    }

    private CommandAPICommand inheritRemove() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("remove")
                .withPermission(this.permission)
                .withArguments(Arguments.rankArgument("parent"))
                .withArguments(Arguments.rankArgument("child"))
                .executes((sender, args) -> {
                    Rank parent = (Rank) args[0];
                    Rank child = (Rank) args[1];

                    if (parent.getInherited().remove(child)) {
                        parent.save();

                        pigeon.broadcast(new ReloadRankPayload(parent));
                    } else {
                        sender.sendMessage(Component.text("The parent rank does not inherit the child rank.")
                                .color(NamedTextColor.RED));
                    }
                });
    }

    private CommandAPICommand setColor() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("setcolor")
                .withPermission(this.permission)
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(Arguments.colorArgument("color"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    ChatColor color = (ChatColor) args[1];

                    rank.setColor(color.asBungee().getColor());
                    rank.save();

                    sender.sendMessage(Component.text("You have set the '" + rank.getName() + "' rank's color to '" + color.name() + "'"));

                    pigeon.broadcast(new ReloadRankPayload(rank));
                });
    }

    private CommandAPICommand list() {
        return new CommandAPICommand("list")
                .withPermission(this.permission)
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Component.text(Config.PLAYER_ONLY.format())
                                .color(NamedTextColor.RED));
                        return;
                    }

                    Player player = (Player) sender;

                    new RankListMenu(player);
                });
    }

    private CommandAPICommand setWeight() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("setweight")
                .withPermission(this.permission)
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(new IntegerArgument("weight"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    Integer weight = (Integer) args[1];

                    rank.setWeight(weight);
                    rank.save();

                    pigeon.broadcast(new ReloadRankPayload(rank));
                });
    }

    private CommandAPICommand create() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("create")
                .withPermission(this.permission)
                .withArguments(new StringArgument("rank"))
                .executes((sender, args) -> {
                    String rankName = (String) args[0];

                    if (Rank.getRank(rankName) != null) {
                        sender.sendMessage(Component.text("A rank with that name already exists.")
                                .color(NamedTextColor.RED));
                        return;
                    }

                    Rank rank = new Rank(rankName, 0, "", Color.WHITE);
                    rank.save();

                    pigeon.broadcast(new ReloadRankPayload(rank));
                });
    }

    private CommandAPICommand delete() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("delete")
                .withPermission(this.permission)
                .withArguments(Arguments.rankArgument("rank"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];

                    rank.delete();

                    pigeon.broadcast(new RemoveRankPayload(rank.getUuid()));
                });
    }

    private CommandAPICommand setPrefix() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("setprefix")
                .withPermission(this.permission)
                .withArguments(rankArgument("rank"))
                .withArguments(new GreedyStringArgument("prefix"))
                .executes((sender, args) -> {
                    Rank rank = (Rank) args[0];
                    String prefix = (String) args[1];

                    rank.setPrefix(prefix);
                    rank.save();

                    pigeon.broadcast(new ReloadRankPayload(rank));
                });
    }
}
