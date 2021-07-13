package com.customwrld.flame.util;

import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.customlib.util.ProfileUtil;
import com.customwrld.flame.Config;
import com.customwrld.commonlib.modules.profile.Profile;
import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Arguments {

//    public static CustomArgument<String> proxyPlayerArgument(String nodeName) {
//        CustomArgument<String> argument = new CustomArgument<>(nodeName, (input) -> input);
//        argument.overrideSuggestions(sender -> {
//            List<String> playerNames = new ArrayList<>();
//
//            for (Map.Entry<String, Server> ent : Server.getServers().entrySet()) {
//                Server server = ent.getValue();
//
//                playerNames.addAll(Arrays.asList(server.getPlayerList().split(", ")));
//            }
//
//            return playerNames.toArray(new String[0]);
//        });
//
//        return argument;
//    }

//    public static CustomArgument<Server> serverArgument(String nodeName) {
//        CustomArgument<Server> argument = new CustomArgument<>(nodeName, (input) -> {
//            Server server = Server.getServerByName(input);
//            if (server == null) {
//                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("You must provide a valid server."));
//            } else {
//                return server;
//            }
//        });
//
//        argument.overrideSuggestions(sender -> {
//            List<String> serverNames = new ArrayList<>();
//
//            for (Map.Entry<String, Server> ent : Server.getServers().entrySet()) {
//                serverNames.add(ent.getKey());
//            }
//
//            return serverNames.toArray(new String[0]);
//        });
//
//        return argument;
//    }

    public static CustomArgument<Duration> durationArgument(String nodeName) {
        CustomArgument<Duration> argument = new CustomArgument<>(nodeName, (input) -> {

            Duration duration = Duration.fromString(input);

            if (duration.getValue() == -1) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("That is not a valid duration."));
            } else {
                return duration;
            }
        });

        argument.overrideSuggestions(Collections.emptyList());

        return argument;
    }

    public static CustomArgument<Player> playerArgument(String nodeName) {
        CustomArgument<Player> argument = new CustomArgument<>(nodeName, (input) -> {

            Player player = Bukkit.getPlayer(input);

            if(player == null) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("That player is currently not online."));
            } else {
                return player;
            }
        });

        argument.overrideSuggestions(sender -> Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));

        return argument;
    }

    public static CustomArgument<Profile> profileArgument(String nodeName) {
        CustomArgument<Profile> argument = new CustomArgument<>(nodeName, input -> {
            try {
                Profile profile = ProfileUtil.getProfile(input).get(10, TimeUnit.SECONDS);


                if (profile == null)
                    throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder(Config.COULD_NOT_RESOLVE_PLAYER.format()));

                return profile;
            } catch(InterruptedException | ExecutionException | TimeoutException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            return null;
        });

        argument.overrideSuggestions(sender -> Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new));

        return argument;
    }

    public static CustomArgument<String> hexArgument(String nodeName) {
        CustomArgument<String> argument = new CustomArgument<>(nodeName, (input) -> {

            boolean valid = input.matches("^[A-Za-z0-9]*$") && input.length() == 6;

            if (valid) {
                return input;
            } else {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("Your provided a invalid hex color."));
            }
        });

        argument.overrideSuggestions(Collections.emptyList());

        return argument;
    }

    public static CustomArgument<ChatColor> colorArgument(String nodeName) {
        CustomArgument<ChatColor> argument = new CustomArgument<>(nodeName, (input) -> {

            for (ChatColor color : ChatColor.values()) {
                if (color.name().equalsIgnoreCase(input)) {
                    return color;
                }
            }

            throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder("A color with that name could not be found."));
        });

        argument.overrideSuggestions(sender -> Arrays.stream(ChatColor.values()).filter(ChatColor::isColor).map(ChatColor::name).toArray(String[]::new));

        return argument;
    }

    public static CustomArgument<Rank> rankArgument(String nodeName) {
        CustomArgument<Rank> argument = new CustomArgument<>(nodeName, (input) -> {

            Rank rank = Rank.getRank(input);

            if (rank == null) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder(Config.RANK_NOT_FOUND.format()));
            } else {
                return rank;
            }
        });

        argument.overrideSuggestions(sender -> Rank.getRanks().values().stream().map(Rank::getName).toArray(String[]::new));

        return argument;
    }

    public static CustomArgument<PotionEnum> potionTypeArgument(String nodeName) {
        CustomArgument<PotionEnum> argument = new CustomArgument<>(nodeName, (input) -> PotionEnum.valueOf(input.toUpperCase()));

        argument.overrideSuggestions(sender -> Arrays.stream(PotionEnum.values()).map(PotionEnum::getDisplay).toArray(String[]::new));

        return argument;
    }
}
