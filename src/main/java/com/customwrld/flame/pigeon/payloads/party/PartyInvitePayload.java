package com.customwrld.flame.pigeon.payloads.party;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartyInvitePayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit UUID target;

    public PartyInvitePayload() {
        payloadId = "party-invite";
    }

    public PartyInvitePayload(UUID initiator, UUID target) {
        this();
        this.initiator = initiator;
        this.target = target;
    }

    @Override
    public void receive() {
    }

}
