package com.customwrld.flame.pigeon.payloads;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter
@Payload
public class MessagePlayerPayload extends BasePayload {

    @Transmit
    UUID player;
    @Transmit
    String message;

    public MessagePlayerPayload() {
        payloadId = "minecraft-messagePlayer";
    }

    public MessagePlayerPayload(UUID player, String message) {
        this();
        this.player = player;
        this.message = message;
    }

    @Override
    public void receive() {
    }

}
