// TODO: Connect this up with Atom
//package com.customwrld.flame.menus.general;
//
//import com.customwrld.customlib.smartinvs.ClickableItem;
//import com.customwrld.customlib.smartinvs.SmartInventory;
//import com.customwrld.customlib.smartinvs.content.InventoryContents;
//import com.customwrld.customlib.smartinvs.content.InventoryProvider;
//import com.customwrld.customlib.smartinvs.content.Pagination;
//import com.customwrld.customlib.smartinvs.content.SlotIterator;
//import com.customwrld.customlib.util.ItemFactory;
//import com.customwrld.customlib.util.ServerUtil;
//import com.customwrld.customlib.util.TimeUtil;
//import com.customwrld.flame.network.Server;
//import com.customwrld.flame.util.Constants;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//
//import java.util.Map;
//
//public class NetworkInfoMenu implements InventoryProvider {
//
//    private final SmartInventory inventory;
//
//    public NetworkInfoMenu(Player player) {
//        inventory = SmartInventory.builder()
//                .provider(this)
//                .size(5, 9)
//                .title("Network Information")
//                .build();
//        inventory.open(player, 0);
//    }
//
//    @Override
//    public void init(Player player, InventoryContents contents) {
//        Pagination pagination = contents.pagination();
//
//        ClickableItem[] items = new ClickableItem[Server.getServers().size()];
//
//        contents.fill(ClickableItem.empty(Constants.FILLER));
//
//        int i = 0;
//
//        for (Map.Entry<String, Server> ent : Server.getServers().entrySet()) {
//            String serverName = ent.getKey();
//            Server server = ent.getValue();
//
//            items[i] = ClickableItem.of(new ItemFactory(Material.BOOK)
//                            .title(CC.GOLD + serverName)
//                            .lores(CC.MENU_BAR, "&6Status: " + CC.GREEN + "Online", "&6Players: &f" + server.getOnline() + "/" + server.getMax(), "&6TPS: &f" + ServerUtil.formatTPS(server.getTps()), "&6Uptime: &f" + TimeUtil.millisToRoundedTime(server.getUptime()), CC.MENU_BAR)
//                            .build(),
//                    e -> {
//                    });
//            i++;
//        }
//
//        pagination.setItems(items);
//        pagination.setItemsPerPage(27);
//
//        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
//
//        contents.set(0, 0, ClickableItem.of(new ItemFactory(Material.ARROW)
//                        .title("&6Previous Page")
//                        .build(),
//                e -> inventory.open(player, pagination.previous().getPage())));
//
//        contents.set(0, 8, ClickableItem.of(new ItemFactory(Material.ARROW)
//                        .title("&6Next Page")
//                        .build(),
//                e -> inventory.open(player, pagination.next().getPage())));
//    }
//
//    @Override
//    public void update(Player player, InventoryContents contents) {
//    }
//}
