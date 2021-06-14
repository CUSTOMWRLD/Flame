package com.customwrld.flame.menus.general;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemFactory;
import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.flame.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ProfileOptionsMenu implements InventoryProvider {

    private final Profile profile;
    private boolean modified = false;

    public ProfileOptionsMenu(Player player) {
        this.profile = Profile.getByUuid(player.getUniqueId());
        SmartInventory inventory = SmartInventory.builder()
                .provider(this)
                .size(3, 9)
                .title("Profile Options").build();
        inventory.open(player);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.empty(Util.FILLER));
        logic(contents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        logic(contents);
    }

    @Override
    public void close(Player player, InventoryContents contents) {
        if (this.modified) {
            this.profile.save();
        }
    }

    private void logic(InventoryContents contents) {
        contents.set(1, 2, ClickableItem.of(new ItemFactory(Material.NAME_TAG)
                        .setName(CC.GREEN + "Private Messages")
                        .setLore(CC.GRAY + "Do you want to receive",
                                CC.GRAY + "private chat messages?",
                                "",
                                this.profile.getOptions().isReceivingConversations() ? CC.AQUA + "✖ " + CC.GREEN + "Yes" : CC.YELLOW + "Yes",
                                !this.profile.getOptions().isReceivingConversations() ? CC.AQUA + "✖ " + CC.GREEN + "No" : CC.YELLOW + "No").build(),
                e -> {
                    this.modified = true;
                    this.profile.getOptions().setReceivingConversations(!this.profile.getOptions().isReceivingConversations());
                    // TODO: Expire all conversations
                }));

        contents.set(1, 4, ClickableItem.of(new ItemFactory(Material.NOTE_BLOCK)
                        .setName(CC.GREEN + "Private Chat Sounds")
                        .setLore(CC.GRAY + "Do you want to be alerted when",
                                CC.GRAY + "someone private chat messages you?",
                                "",
                                this.profile.getOptions().isReceivingMessageSounds() ? CC.AQUA + "✖ " + CC.GREEN + "Yes" : CC.YELLOW + "Yes",
                                !this.profile.getOptions().isReceivingMessageSounds() ? CC.AQUA + "✖ " + CC.GREEN + "No" : CC.YELLOW + "No").build(),
                e -> {
                    this.modified = true;
                    this.profile.getOptions().setReceivingMessageSounds(!this.profile.getOptions().isReceivingMessageSounds());
                }));

        contents.set(1, 6, ClickableItem.of(new ItemFactory(Material.WRITABLE_BOOK)
                        .setName(CC.GREEN + "Public Chat Messages")
                        .setLore(CC.GRAY + "Do you want to receive",
                                CC.GRAY + "public chat messages?",
                                "",
                                this.profile.getOptions().isReceivingPublicChat() ? CC.AQUA + "✖ " + CC.GREEN + "Yes" : CC.YELLOW + "Yes",
                                !this.profile.getOptions().isReceivingPublicChat() ? CC.AQUA + "✖ " + CC.GREEN + "No" : CC.YELLOW + "No").build(),
                e -> {
                    this.modified = true;
                    this.profile.getOptions().setReceivingPublicChat(!this.profile.getOptions().isReceivingPublicChat());
                }));
    }

}