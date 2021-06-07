package com.customwrld.flame.commands.essentials;

import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.MathUtil;
import com.customwrld.customlib.util.ServerUtil;
import com.customwrld.flame.Config;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.spigotmc.TicksPerSecondCommand;

import java.text.CharacterIterator;
import java.text.DecimalFormat;
import java.text.StringCharacterIterator;

public class ServerStatsCommand extends Command {

    public ServerStatsCommand() {
        this.command = "serverstats";
        this.permission = "flame.serverstats";
        this.aliases = new String[]{"lag", "tps"};
    }

    @Override
    public CommandAPICommand command() {
        return new CommandAPICommand(this.command)
                .withPermission(this.permission)
                .withAliases(this.aliases)
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
                    sender.sendMessage(CC.WHITE + "   Max Memory: " + CC.BLUE + MathUtil.readableBytes(runtime.maxMemory()));
                    sender.sendMessage(CC.WHITE + "   Used Memory: " + CC.BLUE + MathUtil.readableBytes(runtime.totalMemory() - runtime.freeMemory()));
                    sender.sendMessage(CC.WHITE + "   Total Memory: " + CC.BLUE + MathUtil.readableBytes(runtime.totalMemory()) + " (" + format.format(((double) runtime.totalMemory() / (double) runtime.maxMemory()) * 100) + "%)");
                    sender.sendMessage(CC.WHITE + "   Free Memory: " + CC.BLUE + MathUtil.readableBytes(runtime.freeMemory()) + " (" + format.format(((double) runtime.freeMemory() / (double) runtime.maxMemory()) * 100) + "%)");
                    sender.sendMessage(" ");
                    sender.sendMessage(CC.AQUA + "- Server World Stats");
                    sender.sendMessage(CC.WHITE + "   World size: " + CC.BLUE + MathUtil.readableBytes(player.getWorld().getWorldFolder().length()));
                    sender.sendMessage(CC.WHITE + "   Loaded chunks in World: " + CC.BLUE + player.getWorld().getLoadedChunks().length);
                    sender.sendMessage(CC.WHITE + "   Living Entities in World: " + CC.BLUE + player.getWorld().getLivingEntities().size());
                    sender.sendMessage(CC.WHITE + "   Entities in World: " + CC.BLUE + player.getWorld().getEntities().size());
                    sender.sendMessage(" ");
                    sender.sendMessage(CC.AQUA + "TPS from last 1m, 5m, 15m: " + ServerUtil.formatTPS(tps[0]) + ", " + ServerUtil.formatTPS(tps[1]) + ", " + ServerUtil.formatTPS(tps[2]));
                    sender.sendMessage(CC.CHAT_BAR);
                });
    }
}
