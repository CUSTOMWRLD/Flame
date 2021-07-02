package com.customwrld.flame.pigeon.payloads.party;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class PartyDebugPayload extends BasePayload {

    public PartyDebugPayload() {
        payloadId = "party-debug";
    }

    @Override
    public void receive() {
    }

}
