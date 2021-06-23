//package com.customwrld.flame.commands.grant;
//
//import com.customwrld.flame.Config;
//import com.customwrld.flame.pigeon.payloads.RecalculatePlayerPayload;
//import com.customwrld.commonlib.modules.profile.Profile;
//import com.customwrld.commonlib.modules.grant.Grant;
//import com.customwrld.flame.profile.rank.Rank;
//import com.customwrld.flame.util.Arguments;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.CommandPermission;
//import org.bukkit.ChatColor;
//import org.bukkit.command.ConsoleCommandSender;
//
//import java.util.UUID;
//
//public class ClearGrantsCommand extends Command {
//
//    public ClearGrantsCommand() {
//        this.command = "cleargrants";
//        this.permission = "flame.cleargrants";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .withArguments(Arguments.profileArgument("player"))
//                .executes((sender, args) -> {
//                    if (!(sender instanceof ConsoleCommandSender)) {
//                        sender.sendMessage(Config.CONSOLE_ONLY.format());
//                        return;
//                    }
//
//                    Profile profile = (Profile) args[0];
//
//                    profile.getGrants().clear();
//                    profile.getGrants().add(new Grant(UUID.randomUUID(), Rank.getDefaultRank(), null, System.currentTimeMillis(), "Default", Integer.MAX_VALUE));
//                    profile.checkGrants();
//                    profile.save();
//
//                    new RecalculatePlayerPayload(profile.getUuid()).send();
//
//                    sender.sendMessage(ChatColor.GREEN + "Cleared grants of " + profile.getName() + "!");
//                });
//    }
//
//}
