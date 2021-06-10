//package com.customwrld.flame.commands.chat;
//
//import com.customwrld.customlib.CustomLib;
//import com.customwrld.customlib.util.CC;
//import com.customwrld.flame.Config;
//import com.customwrld.flame.Flame;
//import com.customwrld.flame.FlameAPI;
//import com.customwrld.flame.chat.StaffChatEnum;
//import com.customwrld.flame.pigeon.payloads.StaffChatPayload;
//import com.customwrld.flame.profile.Profile;
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.arguments.GreedyStringArgument;
//import org.bukkit.entity.Player;
//
//public class AdminChatCommand extends Command {
//
//    public AdminChatCommand() {
//        this.command = "adminchat";
//        this.permission = "flame.adminchat";
//        this.aliases = new String[]{"ac"};
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .withAliases(this.aliases)
//                .withArguments(new GreedyStringArgument("message"))
//                .executes((sender, args) -> {
//                    String playerName = CC.CONSOLE;
//
//                    if (sender instanceof Player) {
//                        Profile profile = Profile.getByUuid(((Player) sender).getUniqueId());
//
//                        if (!profile.getStaffProfile().staffMessagesEnabled()) {
//                            sender.sendMessage(CC.RED + "You currently have staff messages toggled.");
//                            return;
//                        }
//
//                        playerName = FlameAPI.getColoredName(profile);
//                    }
//
//                    new StaffChatPayload(StaffChatEnum.ADMIN_CHAT, Config.SERVER_NAME.format(), playerName, (String) args[0]).send();
//                });
//    }
//
//}
