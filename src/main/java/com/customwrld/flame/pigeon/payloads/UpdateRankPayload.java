package com.customwrld.flame.pigeon.payloads;

import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.rank.Rank;
import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter
@Payload
public class UpdateRankPayload extends BasePayload {

    @Transmit
    UUID rankId;

    public UpdateRankPayload() {
        payloadId = "flame-updateRank";
    }

    public UpdateRankPayload(UUID rankId) {
        this();
        this.rankId = rankId;

        Rank rank = Rank.getRankByUuid(rankId);

        Rank.getRanks().remove(rankId);

        if(rank != null) {
            Rank.getRanks().put(rankId, rank);
        } else {
            // Abort
        }
    }

    @Override
    public void receive() {
    }

    // Remove rank from map
    // Load rank and add back to map

    // TODO: DeleteRankPayload
    // Remove rank from map
    // For each online player load next grant if it is their rank

}
