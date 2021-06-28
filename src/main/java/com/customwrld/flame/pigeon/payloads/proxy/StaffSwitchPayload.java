package com.customwrld.flame.pigeon.payloads.proxy;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class StaffSwitchPayload extends BasePayload {

    @Transmit UUID uuid;
    @Transmit String serverFrom;
    @Transmit String serverTo;

    public StaffSwitchPayload() {
        payloadId = "proxy-staffSwitch";
    }

    public StaffSwitchPayload(UUID uuid, String serverFrom, String serverTo) {
        this();
        this.uuid = uuid;
        this.serverFrom = serverFrom;
        this.serverTo = serverTo;
    }

    @Override
    public void receive() {}

}
