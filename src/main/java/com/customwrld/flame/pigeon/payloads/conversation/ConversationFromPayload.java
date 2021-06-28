package com.customwrld.flame.pigeon.payloads.conversation;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class ConversationFromPayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit UUID target;
    @Transmit String message;

    public ConversationFromPayload() {
        payloadId = "minecraft-conversationFrom";
    }

    public ConversationFromPayload(UUID initiator, UUID target, String message) {
        this();
        this.initiator = initiator;
        this.target = target;
        this.message = message;
    }

    @Override
    public void receive() {}

}
