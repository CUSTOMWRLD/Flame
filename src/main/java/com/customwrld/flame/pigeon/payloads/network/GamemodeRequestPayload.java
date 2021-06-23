package com.customwrld.flame.pigeon.payloads.network;

import com.customwrld.flame.network.Gamemode;
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
public class GamemodeRequestPayload extends FeedbackPayload {

    public GamemodeRequestPayload() {
        this.payloadId = "atom-gamemodeRequestPayload";
        this.payloadTimeout = 10000;
    }

    //Request

    @Transmit(direction = RequiredState.REQUEST) String name;

    @RequestConstructor
    public GamemodeRequestPayload(String name, Consumer<GamemodeRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.name = name;

        this.feedback = feedback;
    }

    //Response

    @Transmit(direction = RequiredState.RESPONSE) Gamemode gamemode;

    @ResponseConstructor
    public GamemodeRequestPayload(Gamemode gamemode) {
        this();
        this.payloadState = FeedbackState.RESPONSE;

        this.gamemode = gamemode;
    }

    @Override
    public void receive() {}

}
