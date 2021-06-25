package com.customwrld.flame.pigeon.payloads.network;

import com.customwrld.flame.network.Gamemode;
import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

@Getter @Payload
public class RegisterGamemodePayload extends BasePayload {

    public RegisterGamemodePayload() {
        this.payloadId = "atom-registerGamemodePayload";
    }

    @Transmit
    Gamemode gamemode;

    public RegisterGamemodePayload(Gamemode gamemode) {
        this();
        this.gamemode = gamemode;
    }

    @Override
    public void receive() {}

}
