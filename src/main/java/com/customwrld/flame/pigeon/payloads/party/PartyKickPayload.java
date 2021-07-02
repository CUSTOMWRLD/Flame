package com.customwrld.flame.pigeon.payloads.party;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartyKickPayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit UUID target;

    public PartyKickPayload() {
        payloadId = "party-kick";
    }

    public PartyKickPayload(UUID initiator, UUID target) {
        this();
        this.initiator = initiator;
        this.target = target;
    }

    @Override
    public void receive() {
    }

}
