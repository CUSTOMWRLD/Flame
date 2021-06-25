//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.flame.Config;
//import com.customwrld.flame.Flame;
//import com.customwrld.commonlib.modules.profile.Profile;
//import com.customwrld.flame.profile.StaffProfile;
//import com.customwrld.flame.util.Arguments;
//import dev.jorel.commandapi.CommandAPICommand;
//import org.bukkit.command.ConsoleCommandSender;
//import org.bukkit.entity.Player;
//
//import java.util.Arrays;
//
//public class TwoFactorAuthCommand extends Command {
//
//    public replace() {
//        this.command = "replace";
//        this.permission = "flame.replace";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return null;
//    }
//
//    public static CommandAPICommand command() {
//        return new CommandAPICommand("2fa")
//                .withAliases("twofactorauth")
//                .withPermission("flame.twofactorauth")
//                .withSubcommand(new CommandAPICommand("enable")
//                        .executes((sender, args) -> {
//                            if (!(sender instanceof Player)) {
//                                sender.sendMessage(Config.PLAYER_ONLY.format());
//                                return;
//                            }
//
//                            Profile profile = Profile.getByUuid(((Player) sender).getUniqueId());
//                            StaffProfile staffProfile = profile.getStaffProfile();
//                            String key = Flame.getFlame().getAuth().createCredentials().getKey();
//
//                            if (profile.getStaffProfile().authEnabled()) {
//                                sender.sendMessage(CC.RED + "You already have two factor authentication enabled!");
//                                return;
//                            }
//
//                            Arrays.asList(
//                                    CC.CHAT_BAR,
//                                    CC.GREEN + "Your authentication code is " + key,
//                                    CC.GREEN + "Enter this code in a authentication app!",
//                                    CC.CHAT_BAR).forEach(sender::sendMessage);
//
//                            staffProfile.authEnabled(true);
//                            staffProfile.authKey(key);
//                            staffProfile.locked(true);
//                            profile.save();
//                        }))
//                .withSubcommand(new CommandAPICommand("disable")
//                        .withArguments(Arguments.profileArgument("player"))
//                        .executes((sender, args) -> {
//                            if (!(sender instanceof ConsoleCommandSender)) {
//                                sender.sendMessage(Config.CONSOLE_ONLY.format());
//                                return;
//                            }
//
//                            Profile profile = (Profile) args[0];
//                            StaffProfile staffProfile = profile.getStaffProfile();
//
//                            if (staffProfile.authEnabled()) {
//                                staffProfile.authEnabled(false);
//                                staffProfile.authKey(null);
//                                staffProfile.locked(false);
//                                profile.save();
//
//                                sender.sendMessage(CC.GREEN + "2FA has been disabled for " + profile.getName() + "!");
//                            } else {
//                                sender.sendMessage(CC.RED + profile.getName() + " does not have 2FA enabled.");
//                            }
//                        }));
//    }
//
//}
