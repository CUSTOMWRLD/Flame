package com.customwrld.flame.pigeon.payloads.friend;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.UUID;

@Getter @Payload
public class DenyFriendRequestPayload extends BasePayload {

    @Transmit UUID initiator;
    @Transmit UUID target;

    public DenyFriendRequestPayload() {
        payloadId = "minecraft-denyFriendRequest";
    }

    public DenyFriendRequestPayload(UUID initiator, UUID target) {
        this();
        this.initiator = initiator;
        this.target = target;
    }

    @Override
    public void receive() {}

}
