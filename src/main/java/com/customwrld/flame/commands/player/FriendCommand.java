package com.customwrld.flame.commands.player;

import com.customwrld.customlib.CustomLib;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import com.customwrld.flame.pigeon.payloads.CreateFriendRequestPayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import com.customwrld.pigeon.Pigeon;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FriendCommand extends Command {

    public FriendCommand() {
        this.command = "friend";
        this.permission = "flame.friend";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withSubcommand(new CommandAPICommand("add")
                        .withArguments(Arguments.profileArgument("profile"))
                        .executes((sender, args) -> {
                            if (!(sender instanceof Player)) {
                                sender.sendMessage(Config.PLAYER_ONLY.format());
                                return;
                            }

                            Pigeon pigeon = CustomLib.getCustomLib().getPigeon();

                            Profile initiator = Profile.getByUuid(((Player) sender).getUniqueId());
                            Profile target = (Profile) args[0];

                            for(UUID uuid : initiator.getFriends()) {
                                if(uuid.equals(target.getUuid())) {
                                    sender.sendMessage(CC.RED + "You already have that player added as a friend.");
                                    return;
                                }
                            }

                            if(target.isIgnoring(initiator) || !target.getOptions().isReceivingFriendRequests()) {
                                sender.sendMessage("&cYou aren't allowed to send friend requests to this person!");
                                return;
                            }

                            // RENAME TO CREATE FRIEND REQUEST AND DO ALL LOGIC WITH ONE REQUEST
                            pigeon.broadcast(new CreateFriendRequestPayload(initiator.getUuid(), target.getUuid(), (payload) -> sender.sendMessage(CC.translate(payload.getResponse()))));

//                            Server server = Server.getServerByPlayer(profile.getName());
//
//                            if (server == null) {
//                                sender.sendMessage(CC.RED + "That player is not online!");
//                                return;
//                            }
//
//                            // Send request
                        }));
    }

}
