package com.customwrld.flame.design;

import com.customwrld.customlib.assemble.AssembleAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

    @Override
    public String getTitle(Player player) {
        return ChatColor.GOLD.toString() + "Assemble Board Example";
    }

    @Override
    public List<String> getLines(Player player) {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("&a&lThis is a line!");

        return toReturn;
    }

}
