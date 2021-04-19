package com.customwrld.flame.rank.comparator;

import com.customwrld.flame.rank.Rank;

import java.util.Comparator;

public class RankComparator
        implements Comparator<Rank> {
    public int compare(Rank rank1, Rank rank2) {
        return rank2.getWeight() - rank1.getWeight();
    }
}