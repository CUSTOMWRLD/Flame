package com.customwrld.flame.pigeon.payloads;

import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter
@Payload
public class GrantDeletePayload extends BasePayload {

    @Transmit
    UUID recipient;
    @Transmit
    Grant grant;

    public GrantDeletePayload() {
        payloadId = "flame-grantDelete";
    }

    public GrantDeletePayload(UUID recipient, Grant grant) {
        this();
        this.recipient = recipient;
        this.grant = grant;
    }

    @Override
    public void receive() {
    }

}
