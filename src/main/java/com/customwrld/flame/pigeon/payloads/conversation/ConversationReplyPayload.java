package com.customwrld.flame.pigeon.payloads.conversation;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter
@Payload
public class ConversationReplyPayload extends BasePayload {

    @Transmit
    UUID initiator;
    @Transmit
    String message;

    public ConversationReplyPayload() {
        payloadId = "minecraft-conversationReply";
    }

    public ConversationReplyPayload(UUID initiator, String message) {
        this();
        this.initiator = initiator;
        this.message = message;
    }

    @Override
    public void receive() {
    }

}