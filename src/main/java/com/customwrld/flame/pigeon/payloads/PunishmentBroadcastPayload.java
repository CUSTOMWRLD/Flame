package com.customwrld.flame.pigeon.payloads;

import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter
@Payload
public class PunishmentBroadcastPayload extends BasePayload {

    @Transmit
    String punishmentId;
    @Transmit
    UUID offender;

    public PunishmentBroadcastPayload() {
        payloadId = "flame-punishmentBroadcast";
    }

    public PunishmentBroadcastPayload(String punishmentId, UUID offender) {
        this();
        this.punishmentId = punishmentId;
        this.offender = offender;

        Punishment punishment = Punishment.getById(punishmentId);
        Profile profile = Profile.getByUuid(offender);

        if(punishment != null) {
            punishment.broadcast(profile);
        }
    }

    @Override
    public void receive() {
    }

}
