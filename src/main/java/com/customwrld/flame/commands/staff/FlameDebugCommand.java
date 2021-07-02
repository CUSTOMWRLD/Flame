package com.customwrld.flame.commands.staff;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.util.MC;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LongArgument;
import net.kyori.adventure.text.Component;

public class FlameDebugCommand extends Command {

    public FlameDebugCommand() {
        this.command = "flamedebug";
        this.permission = "flame.debug";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withSubcommand(new CommandAPICommand("commands")
                        .executes((sender, args) -> {

                        }))
                .withSubcommand(new CommandAPICommand("level")
                        .withArguments(Arguments.profileArgument("profile"))
                        .withArguments(new LongArgument("level"))
                        .executes((sender, args) -> {
                            Profile profile = (Profile) args[0];
                            Long exp = (Long) args[1];

                            profile.setExperience(exp);
                            sender.sendMessage(Component.text("You set " + profile.getName() + "'s EXP to " + exp, MC.CC.GREEN.getTextColor()));
                        }))
                .withSubcommand(new CommandAPICommand("gold")
                        .withArguments(Arguments.profileArgument("profile"))
                        .withArguments(new IntegerArgument("gold"))
                        .executes((sender, args) -> {
                            Profile profile = (Profile) args[0];
                            Integer gold = (Integer) args[1];

                            profile.setGold(gold);
                            sender.sendMessage(Component.text("You set " + profile.getName() + "'s Gold to " + gold, MC.CC.GREEN.getTextColor()));
                        }));
    }
}
