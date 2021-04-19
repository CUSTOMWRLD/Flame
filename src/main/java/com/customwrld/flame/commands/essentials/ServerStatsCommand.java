package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Config;
import dev.jorel.commandapi.CommandAPICommand;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;

public class ServerStatsCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("serverstats")
                .withAliases("lag")
                .withPermission("flame.serverstats")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    Player player = (Player) sender;

                    double[] tps = MinecraftServer.getServer().recentTps;
                    Runtime runtime = Runtime.getRuntime();
                    DecimalFormat format = new DecimalFormat("###.##");

                    sender.sendMessage(CC.CHAT_BAR);
                    sender.sendMessage(CC.AQUA + "- Server Memory Stats");
                    sender.sendMessage(CC.WHITE + "   Max Memory: " + CC.BLUE + readableBytes(runtime.maxMemory()));
                    sender.sendMessage(CC.WHITE + "   Used Memory: " + CC.BLUE + readableBytes(runtime.totalMemory() - runtime.freeMemory()));
                    sender.sendMessage(CC.WHITE + "   Total Memory: " + CC.BLUE + readableBytes(runtime.totalMemory()) + " (" + format.format(((double) runtime.totalMemory() / (double) runtime.maxMemory()) * 100) + "%)");
                    sender.sendMessage(CC.WHITE + "   Free Memory: " + CC.BLUE + readableBytes(runtime.freeMemory()) + " (" + format.format(((double) runtime.freeMemory() / (double) runtime.maxMemory()) * 100) + "%)");
                    sender.sendMessage(" ");
                    sender.sendMessage(CC.AQUA + "- Server World Stats");
                    sender.sendMessage(CC.WHITE + "   World size: " + CC.BLUE + readableBytes(player.getWorld().getWorldFolder().length()));
                    sender.sendMessage(CC.WHITE + "   Loaded chunks in World: " + CC.BLUE + player.getWorld().getLoadedChunks().length);
                    sender.sendMessage(CC.WHITE + "   Living Entities in World: " + CC.BLUE + player.getWorld().getLivingEntities().size());
                    sender.sendMessage(CC.WHITE + "   Entities in World: " + CC.BLUE + player.getWorld().getEntities().size());
                    sender.sendMessage(" ");
                    sender.sendMessage(CC.AQUA + "TPS from last 1m, 5m, 15m: " + format(tps[0]) + ", " + format(tps[1]) + ", " + format(tps[2]));
                    sender.sendMessage(CC.CHAT_BAR);
                });
    }

    private static String readableBytes(long bytes) { // TODO Merge in to CustomLib
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    private static String format(double tps) {
        return (tps > 18.0D ? ChatColor.GREEN : (tps > 16.0D ? ChatColor.YELLOW : ChatColor.RED)).toString() + (tps > 20.0D ? "*" : "") + Math.min((double) Math.round(tps * 100.0D) / 100.0D, 20.0D);
    }

}
