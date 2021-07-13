package com.customwrld.flame.commands.player;

import com.customwrld.commonlib.modules.pigeon.payloads.party.*;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.Config;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.pigeon.Pigeon;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

public class PartyCommand extends Command {

    public PartyCommand() {
        this.command = "party";
        this.aliases = new String[]{"p"};
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withAliases(this.aliases)
                .withSubcommand(chat())
                .withSubcommand(disband())
                .withSubcommand(invite())
                .withSubcommand(join())
                .withSubcommand(kick())
                .withSubcommand(leave());
    }

    private CommandAPICommand chat() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("chat")
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    String message = (String) args[0];

                    pigeon.broadcast(new PartyChatPayload(player.getUniqueId(), message));
                });
    }

    private CommandAPICommand invite() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("invite")
                .withArguments(Arguments.profileArgument("profile"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Profile profile = (Profile) args[0];

                    pigeon.broadcast(new PartyInvitePayload(player.getUniqueId(), profile.getId()));
                });
    }

    private CommandAPICommand kick() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("kick")
                .withArguments(Arguments.profileArgument("profile"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Profile profile = (Profile) args[0];

                    pigeon.broadcast(new PartyKickPayload(player.getUniqueId(), profile.getId()));
                });
    }

    private CommandAPICommand leave() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("leave")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    pigeon.broadcast(new PartyLeavePayload(player.getUniqueId()));
                });
    }

    private CommandAPICommand join() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("join")
                .withArguments(Arguments.profileArgument("profile"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Profile profile = (Profile) args[0];

                    pigeon.broadcast(new PartyJoinPayload(player.getUniqueId(), profile.getId()));
                });
    }

    private CommandAPICommand disband() {
        Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

        return new CommandAPICommand("disband")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    pigeon.broadcast(new PartyDisbandPayload(player.getUniqueId()));
                });
    }

}
