package com.customwrld.flame.pigeon;

import com.customwrld.commonlib.modules.rank.Rank;
import com.customwrld.flame.FlameAPI;
import com.customwrld.flame.pigeon.payloads.rank.ReloadRankPayload;
import com.customwrld.flame.pigeon.payloads.rank.RemoveRankPayload;
import com.customwrld.pigeon.annotations.PayloadHandler;
import com.customwrld.pigeon.annotations.PayloadListener;
import com.customwrld.pigeon.listeners.Listener;

@PayloadListener
public class Listeners implements Listener {

    @PayloadHandler
    public void onReloadRank(ReloadRankPayload payload) {
        Rank rank = Rank.getRank(payload.getRank());

        Rank.getRanks().remove(payload.getRank());

        if(rank != null) {
            Rank.getRanks().put(rank.getUuid(), rank);
        }
    }

    @PayloadHandler
    public void onRemoveRank(RemoveRankPayload payload) {
        Rank.getRanks().remove(payload.getRank());

        FlameAPI.refreshPlayerGrants();
    }

}
