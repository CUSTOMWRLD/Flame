package com.customwrld.flame.commands.grant;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.rank.Rank;
import com.customwrld.flame.util.Arguments;
import com.customwrld.flame.util.Duration;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetRankCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("setrank")
                .withPermission("flame.setrank")
                .withArguments(Arguments.profileArgument("player"))
                .withArguments(Arguments.rankArgument("rank"))
                .withArguments(Arguments.durationArgument("duration"))
                .withArguments(new GreedyStringArgument("reason"))
                .executes((sender, args) -> {
                    Profile profile = (Profile) args[0];
                    Rank rank = (Rank) args[1];
                    Duration duration = (Duration) args[2];
                    String reason = (String) args[3];

                    if (rank.isDefaultRank()) {
                        sender.sendMessage(CC.RED + "You cannot grant a player the default rank.");
                        return;
                    }

                    UUID addedBy = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
                    Grant grant = new Grant(UUID.randomUUID(), rank, addedBy, System.currentTimeMillis(), reason, duration.getValue());

                    profile.getGrants().add(grant);
                    profile.save();
                    profile.checkGrants();

                    sender.sendMessage(CC.GREEN +
                            "You applied a '" + rank.getName() +
                            "' grant to '" + profile.getName() +
                            "' for " +
                            (duration.getValue() == Integer.MAX_VALUE ? "forever" : TimeUtil.millisToRoundedTime(duration.getValue())) + ".");


                    Player player = profile.getPlayer();

                    if (player != null) {
                        Grant.loadNewGrant(profile, player, grant);
                    }
                });
    }

}
