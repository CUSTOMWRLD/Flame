package com.customwrld.flame.pigeon.payloads;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.RequestConstructor;
import com.customwrld.pigeon.annotations.ResponseConstructor;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.feedback.FeedbackState;
import com.customwrld.pigeon.feedback.RequiredState;
import com.customwrld.pigeon.payloads.bases.FeedbackPayload;
import lombok.Getter;

import java.util.UUID;
import java.util.function.Consumer;

@Getter @Payload
public class OnlinePlayerRequestPayload extends FeedbackPayload {

    @Transmit UUID player;

    @Transmit(direction = RequiredState.RESPONSE)

    boolean isOnline;

    public OnlinePlayerRequestPayload() {
        this.payloadId = "atom-onlinePlayerRequest";
        this.payloadTimeout = 10000;
    }

    @RequestConstructor
    public OnlinePlayerRequestPayload(UUID player, Consumer<OnlinePlayerRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.feedback = feedback;
    }

    @ResponseConstructor
    public OnlinePlayerRequestPayload(boolean isOnline) {
        this();
        this.payloadState = FeedbackState.RESPONSE;

        this.isOnline = isOnline;
    }

    @Override
    public void receive() {
    }

}
