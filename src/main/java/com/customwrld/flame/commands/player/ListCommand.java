package com.customwrld.flame.commands.player;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.rank.Rank;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListCommand extends Command {

    public ListCommand() {
        this.command = "list";
        this.permission = "flame.list";
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .executes((sender, args) -> {
                    List<Player> sortedPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    sortedPlayers.sort((player1, player2) -> {
                        Profile profile1 = Profile.getByUuid(player1.getUniqueId());
                        Profile profile2 = Profile.getByUuid(player2.getUniqueId());
                        return profile2.getActiveRank().getWeight() - profile1.getActiveRank().getWeight();
                    });

                    List<String> playerNames = new ArrayList<>();

                    for (Player player : sortedPlayers) {
                        Profile profile = Profile.getByUuid(player.getUniqueId());
                        playerNames.add(FlameAPI.getColoredName(profile));
                    }

                    List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
                    sortedRanks.sort(Rank.COMPARATOR);

                    List<String> rankNames = new ArrayList<>();

                    for (Rank rank : sortedRanks) {
                        rankNames.add(rank.getColor() + rank.getName());
                    }

                    sender.sendMessage(CC.CHAT_BAR);
                    sender.sendMessage(StringUtils.join(rankNames, CC.GRAY + ", "));
                    sender.sendMessage(CC.CHAT_BAR);
                    sender.sendMessage(CC.GRAY + "There are " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + " players online:");
                    sender.sendMessage(StringUtils.join(playerNames, CC.GRAY + ", "));
                    sender.sendMessage(CC.CHAT_BAR);
                });
    }

}
