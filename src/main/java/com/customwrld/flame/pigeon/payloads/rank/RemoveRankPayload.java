package com.customwrld.flame.pigeon.payloads.rank;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class RemoveRankPayload extends BasePayload {

    @Transmit UUID rank;

    public RemoveRankPayload() {
        payloadId = "flame-removeRank";
    }

    public RemoveRankPayload(UUID rank) {
        this();
        this.rank = rank;
    }

    @Override
    public void receive() {
    }

}
