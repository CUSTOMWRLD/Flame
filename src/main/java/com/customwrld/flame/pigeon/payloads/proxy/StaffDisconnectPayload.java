package com.customwrld.flame.pigeon.payloads.proxy;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class StaffDisconnectPayload extends BasePayload {

    @Transmit UUID uuid;
    @Transmit String server;

    public StaffDisconnectPayload() {
        payloadId = "proxy-staffDisconnect";
    }

    public StaffDisconnectPayload(UUID uuid, String server) {
        this();
        this.uuid = uuid;
        this.server = server;
    }

    @Override
    public void receive() {}

}
