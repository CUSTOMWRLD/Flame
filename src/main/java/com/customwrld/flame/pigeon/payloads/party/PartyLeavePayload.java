package com.customwrld.flame.pigeon.payloads.party;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PartyLeavePayload extends BasePayload {

    @Transmit UUID initiator;

    public PartyLeavePayload() {
        payloadId = "party-leave";
    }

    public PartyLeavePayload(UUID initiator) {
        this();
        this.initiator = initiator;
    }

    @Override
    public void receive() {
    }

}
