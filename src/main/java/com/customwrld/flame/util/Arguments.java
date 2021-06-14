package com.customwrld.flame.util;

import com.customwrld.flame.Config;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.profile.rank.Rank;
import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

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

    public static CustomArgument<Profile> profileArgument(String nodeName) {
        CustomArgument<Profile> argument = new CustomArgument<>(nodeName, (input) -> {

            Profile profile = Profile.getByUsername(input);

            if (profile == null || !profile.isLoaded()) {
                throw new CustomArgument.CustomArgumentException(new CustomArgument.MessageBuilder(Config.COULD_NOT_RESOLVE_PLAYER.format()));
            } else {
                return profile;
            }
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

    public static CustomArgument<Rank> rankArgument(String nodeName) {
        CustomArgument<Rank> argument = new CustomArgument<>(nodeName, (input) -> {

            Rank rank = Rank.getRankByName(input);

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
