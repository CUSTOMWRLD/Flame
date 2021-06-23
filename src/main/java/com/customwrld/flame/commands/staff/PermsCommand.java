//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.customlib.util.CC;
//import com.customwrld.customlib.util.TextSplitter;
//import com.customwrld.flame.Config;
//import com.customwrld.flame.FlameAPI;
//import com.customwrld.flame.pigeon.payloads.RecalculatePlayerPayload;
//import com.customwrld.commonlib.modules.profile.Profile;
//import com.customwrld.flame.util.Arguments;
//import dev.jorel.commandapi.CommandAPICommand;
//import dev.jorel.commandapi.arguments.StringArgument;
//import org.apache.commons.lang.StringUtils;
//import org.bukkit.command.ConsoleCommandSender;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PermsCommand extends Command {
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
//        return new CommandAPICommand("perms")
//                .withPermission("flame.perms")
//                .withSubcommand(check())
//                .withSubcommand(add())
//                .withSubcommand(remove())
//                .withSubcommand(clear());
//    }
//
//    private static CommandAPICommand check() {
//        return new CommandAPICommand("check")
//                .withPermission("flame.perms.check")
//                .withArguments(Arguments.profileArgument("player"))
//                .executes((sender, args) -> {
//                    Profile profile = (Profile) args[0];
//
//                    List<String> toSend = new ArrayList<>();
//                    toSend.add(CC.CHAT_BAR);
//                    toSend.add(CC.GOLD + "Permissions for " + CC.RESET + FlameAPI.getColoredName(profile));
//
//                    List<String> permissions = profile.getAllPermissions();
//                    toSend.add(CC.YELLOW + "All: " + CC.RESET + "(" + permissions.size() + ")");
//                    if (!permissions.isEmpty()) {
//                        toSend.addAll(TextSplitter.split(46, StringUtils.join(permissions, " "), "", ", "));
//                    }
//
//                    List<String> ranks = profile.getRankPermissions();
//
//                    toSend.add(CC.YELLOW + "Ranks: " + CC.RESET + "(" + ranks.size() + ")");
//
//                    if (!ranks.isEmpty()) {
//                        toSend.addAll(TextSplitter.split(46, StringUtils.join(ranks, " "), "", ", "));
//                    }
//
//                    List<String> assigned = profile.getPermissions();
//
//                    toSend.add(CC.YELLOW + "Assigned: " + CC.RESET + "(" + assigned.size() + ")");
//
//                    if (!assigned.isEmpty()) {
//                        toSend.addAll(TextSplitter.split(46, StringUtils.join(assigned, " "), "", ", "));
//                    }
//
//                    toSend.add(CC.CHAT_BAR);
//
//                    for (String line : toSend) {
//                        sender.sendMessage(line);
//                    }
//                });
//    }
//
//    private static CommandAPICommand add() {
//        return new CommandAPICommand("add")
//                .withPermission("flame.perms.add")
//                .withArguments(Arguments.profileArgument("player"))
//                .withArguments(new StringArgument("permission"))
//                .executes((sender, args) -> {
//                    Profile profile = (Profile) args[0];
//                    String permission = ((String) args[1]).toLowerCase().trim();
//
//                    if (profile.getPermissions().contains(permission)) {
//                        sender.sendMessage(CC.RED + "That player already has that permission.");
//                        return;
//                    }
//
//                    profile.getPermissions().add(permission);
//                    profile.save();
//
//                    sender.sendMessage(CC.GREEN + "Successfully added permission " + permission + " for " + profile.getName() + ".");
//                    new RecalculatePlayerPayload(profile.getUuid()).send();
//                });
//    }
//
//    private static CommandAPICommand remove() {
//        return new CommandAPICommand("remove")
//                .withPermission("flame.perms.remove")
//                .withArguments(Arguments.profileArgument("player"))
//                .withArguments(new StringArgument("permission"))
//                .executes((sender, args) -> {
//                    Profile profile = (Profile) args[0];
//                    String permission = ((String) args[1]).toLowerCase().trim();
//
//                    if (!profile.getPermissions().contains(permission)) {
//                        sender.sendMessage(CC.RED + "That player is not assigned that permission.");
//                        return;
//                    }
//
//                    profile.getPermissions().remove(permission);
//                    profile.save();
//
//                    sender.sendMessage(CC.GREEN + "Successfully removed permission " + permission + " from " + profile.getName() + ".");
//                    new RecalculatePlayerPayload(profile.getUuid()).send();
//                });
//    }
//
//    private static CommandAPICommand clear() {
//        return new CommandAPICommand("clear")
//                .withPermission("flame.perms.clear")
//                .withArguments(Arguments.profileArgument("player"))
//                .executes((sender, args) -> {
//                    Profile profile = (Profile) args[0];
//
//                    if (!(sender instanceof ConsoleCommandSender)) {
//                        sender.sendMessage(Config.CONSOLE_ONLY.format());
//                        return;
//                    }
//
//                    if (profile.getPermissions().isEmpty()) {
//                        sender.sendMessage(CC.RED + "That player doesn't have any permissions assigned to them.");
//                        return;
//                    }
//
//                    profile.getPermissions().clear();
//                    profile.save();
//
//                    sender.sendMessage(CC.GREEN + "Successfully cleared permissions for " + profile.getName() + ".");
//                    new RecalculatePlayerPayload(profile.getUuid()).send();
//                });
//    }
//}
