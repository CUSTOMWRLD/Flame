package com.customwrld.flame.pigeon;

import com.customwrld.flame.pigeon.payloads.rank.ReloadRankPayload;
import com.customwrld.flame.pigeon.payloads.rank.RemoveRankPayload;
import com.customwrld.flame.profile.rank.Rank;
import com.customwrld.pigeon.annotations.PayloadHandler;
import com.customwrld.pigeon.annotations.PayloadListener;
import com.customwrld.pigeon.listeners.Listener;

@PayloadListener
public class Listeners implements Listener {

    @PayloadHandler
    public void onReloadRank(ReloadRankPayload payload) {
        Rank rank = Rank.getById(payload.getRank());

        Rank.getRanks().remove(payload.getRank());

        if(rank != null) {
            Rank.getRanks().put(rank.getUuid(), rank);
        }
    }

    @PayloadHandler
    public void onRemoveRank(RemoveRankPayload payload) {
        Rank rank = Rank.getRanks().get(payload.getRank());

        Rank.getRanks().remove(payload.getRank());

        if(rank != null) {
            rank.refreshPlayers();
        }
    }

}
