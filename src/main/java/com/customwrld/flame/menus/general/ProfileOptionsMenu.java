package com.customwrld.flame.menus.general;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.SlotPos;
import com.customwrld.customlib.util.CC;
import com.customwrld.flame.profile.OptionsProfile;
import com.customwrld.flame.profile.Profile;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.Containers;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenWindow;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ProfileOptionsMenu implements InventoryProvider {

    private final Profile profile;

    public ProfileOptionsMenu(Player player) {
        this.profile = Profile.getByUuid(player.getUniqueId());
        SmartInventory inventory = SmartInventory.builder()
                .provider(this)
                .size(3, 9)
                .title(buildTitle()).build();
        inventory.open(player);
    }

    public String buildTitle() {
        OptionsProfile options = profile.getOptions();
        return CC.WHITE + "\uF801\uE8CD\uF802\uE8C2" +
                "\uF803" + (options.conversationsEnabled() ? "\uE8CE" : "\uE8CF") +
                "\uF804" + (options.soundsEnabled() ? "\uE8D2" : "\uE8D3") +
                "\uF805" + (options.publicChatEnabled() ? "\uE8D0" : "\uE8D1");
    }

    public void sendPacket(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(entityPlayer.activeContainer.windowId, Containers.GENERIC_9X3, new ChatMessage(buildTitle(), new Object[0])));
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        OptionsProfile options = profile.getOptions();

        for (int i = 0; i <= 3; i++) {
            contents.set(SlotPos.of(0, i), ClickableItem.of(null, e -> {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                options.conversationsEnabled(!options.conversationsEnabled());
                sendPacket(player);
            }));

            contents.set(SlotPos.of(1, i), ClickableItem.of(null, e -> {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                options.soundsEnabled(!options.soundsEnabled());
                sendPacket(player);
            }));
        }

        for (int i = 5; i <= 8; i++) {
            contents.set(SlotPos.of(0, i), ClickableItem.of(null, e -> {
                player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                options.publicChatEnabled(!options.publicChatEnabled());
                sendPacket(player);
            }));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
