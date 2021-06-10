package com.customwrld.flame.pigeon.payloads;

import com.customwrld.flame.profile.Profile;
import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter
@Payload
public class RecalculateProfilePayload extends BasePayload {

    @Transmit
    UUID player;

    public RecalculateProfilePayload() {
        payloadId = "minecraft-recalculateProfile";
    }

    public RecalculateProfilePayload(UUID player) {
        this();
        this.player = player;

        Profile profile = Profile.getProfiles().get(player);

        if(profile != null && profile.isLoaded()) {
            profile.reload();
        }
    }

    @Override
    public void receive() {
    }

}
