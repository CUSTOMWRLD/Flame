package com.customwrld.flame.commands.punishments;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.flame.profile.punishment.PunishmentType;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.command.ConsoleCommandSender;

public class UnblacklistCommand extends Command {

    public UnblacklistCommand() {
        this.command = "unblacklist";
        this.permission = "flame.unblacklist";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Profile profile = (Profile) args[0];
                    String reason = (String) args[1];

                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Config.CONSOLE_ONLY.format());
                        return;
                    }

                    if (profile.getActivePunishmentByType(PunishmentType.BLACKLIST) == null) {
                        sender.sendMessage(CC.RED + "That player does not have an existing punishment.");
                        return;
                    }

                    Punishment punishment = profile.getActivePunishmentByType(PunishmentType.BLACKLIST);
                    punishment.setRemovedAt(System.currentTimeMillis());
                    punishment.setRemovedReason(reason);
                    punishment.setRemoved(true);

                    profile.save();

                    boolean silent = reason.contains("-s");

                    if (sender.hasPermission("flame.bypasssilent")) {
                        new PunishmentBroadcastPayload(punishment, profile.getUuid(), silent).send();
                    } else {
                        new PunishmentBroadcastPayload(punishment, profile.getUuid(), true).send();
                    }

                    punishment.sendEmbed(profile.getUuid());
                });
    }

}
