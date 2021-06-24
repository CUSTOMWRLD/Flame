package com.customwrld.flame.commands.player;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.customlib.util.MC;
import com.customwrld.flame.FlameAPI;
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
                        Profile profile1 = Profile.getProfile(null, player1.getUniqueId());
                        Profile profile2 = Profile.getProfile(null, player2.getUniqueId());
                        return profile2.api().getActiveGrant().api().getRank().getWeight() - profile1.api().getActiveGrant().api().getRank().getWeight();
                    });

                    List<Component> playerComponents = new ArrayList<>();

                    for (Player player : sortedPlayers) {
                        Profile profile = Profile.getProfile(null, player.getUniqueId());
                        playerComponents.add(FlameAPI.getColoredName(profile));
                    }

                    List<Rank> sortedRanks = new ArrayList<>(Rank.getRanks().values());
                    sortedRanks.sort(Rank.COMPARATOR);

                    List<Component> rankComponents = new ArrayList<>();

                    for (Rank rank : sortedRanks) {
                        rankComponents.add(Component.text(rank.getName()).color(TextColor.color(rank.getColor().getRGB())));
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
