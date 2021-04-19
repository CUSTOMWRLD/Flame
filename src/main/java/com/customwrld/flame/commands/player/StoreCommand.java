package com.customwrld.flame.commands.player;

import com.customwrld.customlib.util.AlignUtil;
import com.customwrld.customlib.util.ChatUtil;
import com.customwrld.flame.Flame;
import com.customwrld.flame.Config;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

public class StoreCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("store")
                .withPermission(CommandPermission.NONE)
                .withAliases("buy")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Config.PLAYER_ONLY.format());
                        return;
                    }

                    TextComponent message = new TextComponent();

                    message.addExtra(ChatUtil.SEPERATOR);

                    message.addExtra("\n");

                    TextComponent component = new TextComponent("Web Store\n");
                    component.setColor(ChatColor.GOLD);
                    component.setBold(true);
                    component.setItalic(true);
                    message.addExtra(AlignUtil.centerAlignMessage(component));

                    String domain = Flame.get().getStoreManager().getDomain();

                    component = new TextComponent("Click here to visit!\n");
                    component.setColor(ChatColor.YELLOW);
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new BaseComponent[]{ChatUtil.builderToComponent(new ComponentBuilder(domain).italic(true))})));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, domain));
                    message.addExtra(AlignUtil.centerAlignMessage(component));

                    message.addExtra(ChatUtil.SEPERATOR);

                    sender.sendMessage(message);
                });
    }
}
