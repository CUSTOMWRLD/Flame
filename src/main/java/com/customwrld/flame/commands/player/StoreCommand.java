package com.customwrld.flame.commands.player;

import com.customwrld.commonlib.util.Minecraft;
import com.customwrld.flame.Config;
import com.customwrld.flame.Flame;
import com.customwrld.flame.util.Command;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class StoreCommand extends Command {

    public StoreCommand() {
        this.command = "store";
        this.permission = "flame.store";
        this.aliases = new String[]{"buy"};
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

                    String domain = Flame.getFlame().getStoreManager().getDomain();

                    TextComponent component = Component.text("You can visit our store at: ")
                            .color(NamedTextColor.GRAY)
                            .append(Component.text(domain)
                                    .color(NamedTextColor.AQUA)
                                    .decoration(TextDecoration.UNDERLINED, true));

                    sender.sendMessage(Minecraft.Style.SEPARATOR);
                    sender.sendMessage(component
                            .color(NamedTextColor.GREEN)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to open the store!")))
                            .clickEvent(ClickEvent.openUrl(domain)));
                    sender.sendMessage(Minecraft.Style.SEPARATOR);
                });
    }
}
