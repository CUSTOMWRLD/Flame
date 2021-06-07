package com.customwrld.flame.pigeon.payloads;

import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PunishmentBroadcastPayload extends BasePayload {

    @Transmit Punishment punishment;
    @Transmit UUID offender;
    @Transmit Boolean silent;

    public PunishmentBroadcastPayload() {
        payloadId = "flame-punishmentBroadcast";
    }

    public PunishmentBroadcastPayload(Punishment punishment, UUID offender, Boolean silent) {
        this();
        this.punishment = punishment;
        this.offender = offender;
        this.silent = silent;
    }

    @Override
    public void receive() {}

}
