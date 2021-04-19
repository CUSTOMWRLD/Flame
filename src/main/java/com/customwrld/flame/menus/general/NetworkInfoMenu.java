package com.customwrld.flame.menus.general;

import com.customwrld.customlib.smartinvs.ClickableItem;
import com.customwrld.customlib.smartinvs.SmartInventory;
import com.customwrld.customlib.smartinvs.content.InventoryContents;
import com.customwrld.customlib.smartinvs.content.InventoryProvider;
import com.customwrld.customlib.smartinvs.content.Pagination;
import com.customwrld.customlib.smartinvs.content.SlotIterator;
import com.customwrld.customlib.util.CC;
import com.customwrld.customlib.util.ItemBuilder;
import com.customwrld.customlib.util.TimeUtil;
import com.customwrld.flame.network.Server;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

public class NetworkInfoMenu implements InventoryProvider {

    private SmartInventory inventory;

    public NetworkInfoMenu(Player player) {
        inventory = SmartInventory.builder()
                .id("networkInfoMenu")
                .provider(this)
                .size(5, 9)
                .title("Network Information")
                .build();
        inventory.open(player, 0);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[Server.getServers().size()];

        contents.fill(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .title(" ")
                .build()));

        int i = 0;

        for (Map.Entry<String, Server> ent : Server.getServers().entrySet()) {
            String serverName = ent.getKey();
            Server server = ent.getValue();
            String status;

            if (server.isWhitelisted()) {
                status = StatusEnum.WHITELISTED.getReadable();
            } else {
                status = StatusEnum.ONLINE.getReadable();
            }
            double tps = server.getTps();
            String readableTps = ((tps > 18.0) ? CC.GREEN : (tps > 16.0) ? CC.YELLOW : CC.RED) + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);

            items[i] = ClickableItem.of(new ItemBuilder(Material.BOOK)
                            .title(CC.GOLD + serverName)
                            .lores(CC.MENU_BAR, "&6Players: &f" + server.getOnline() + "/" + server.getMax(), "&6TPS: &f" + readableTps, "&6Status: " + status, "&6Uptime: &f" + TimeUtil.millisToDHMS(server.getUptime()), CC.MENU_BAR)
                            .build(),
                    e -> {
                    });
            i++;
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(27);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        contents.set(0, 0, ClickableItem.of(new ItemBuilder(Material.ARROW)
                        .title("&6Previous Page")
                        .build(),
                e -> inventory.open(player, pagination.previous().getPage())));

        contents.set(0, 8, ClickableItem.of(new ItemBuilder(Material.ARROW)
                        .title("&6Next Page")
                        .build(),
                e -> inventory.open(player, pagination.next().getPage())));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
