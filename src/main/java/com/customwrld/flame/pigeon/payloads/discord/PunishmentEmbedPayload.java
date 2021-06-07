package com.customwrld.flame.pigeon.payloads.discord;

import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class PunishmentEmbedPayload extends BasePayload {

    @Transmit UUID player;
    @Transmit Punishment punishment;

    public PunishmentEmbedPayload() {
        payloadId = "discord-punishmentEmbed";
    }

    public PunishmentEmbedPayload(UUID player, Punishment punishment) {
        this();
        this.player = player;
        this.punishment = punishment;
    }

    @Override
    public void receive() {}

}
