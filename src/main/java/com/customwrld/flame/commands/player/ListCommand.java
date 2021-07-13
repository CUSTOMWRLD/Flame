package com.customwrld.flame.commands.player;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.commonlib.util.MC;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.listeners.ChatListener;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
                        Profile profile1 = ProfileUtil.getAssociatedProfile(player1, false, false).getNow(null);
                        int weight1 = profile1 != null ? profile1.api().getActiveGrant().api().getRank().getWeight() : 100;

                        Profile profile2 = ProfileUtil.getAssociatedProfile(player2, false, false).getNow(null);
                        int weight2 = profile2 != null ? profile2.api().getActiveGrant().api().getRank().getWeight() : 100;

                        return weight1 - weight2;
                    });

                    List<Component> playerComponents = new ArrayList<>();

                    for (Player player : sortedPlayers) {
                        Profile profile = ProfileUtil.getAssociatedProfile(player, false, false).getNow(null);
                        if(profile != null)
                            playerComponents.add(ChatListener.hoverableFormat(profile));
                    }

                    List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
                    sortedRanks.sort(Rank.COMPARATOR);

                    List<Component> rankComponents = new ArrayList<>();

                    for (Rank rank : sortedRanks) {
                        rankComponents.add(Component.text(rank.getName()).color(TextColor.color(rank.api().getRankColor().getRGB())));
                    }

                    sender.sendMessage(MC.Style.SEPARATOR);
                    sender.sendMessage(Component.join(Component.text(", ")
                            .color(NamedTextColor.GRAY), rankComponents));
                    sender.sendMessage(MC.Style.SEPARATOR);
                    sender.sendMessage(Component.text("There are " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + " players online:")
                            .color(NamedTextColor.GRAY));
                    sender.sendMessage(Component.join(Component.text(", ")
                            .color(NamedTextColor.GRAY), playerComponents));
                    sender.sendMessage(MC.Style.SEPARATOR);
                });
    }

}
