package com.customwrld.flame.pigeon.payloads.party;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.UUID;

@Getter @Payload
public class PartyChatPayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit String message;

    public PartyChatPayload() {
        payloadId = "party-chat";
    }

    public PartyChatPayload(UUID initiator, String message) {
        this();
        this.initiator = initiator;
        this.message = message;
    }

    @Override
    public void receive() {
    }

}
