package com.customwrld.flame.pigeon.payloads;

import com.customwrld.pigeon.annotations.Payload;
import com.customwrld.pigeon.annotations.Transmit;
import com.customwrld.pigeon.payloads.bases.BasePayload;
import lombok.Getter;

import java.util.List;

@Getter @Payload
public class ServerUpdatePayload extends BasePayload {

    @Transmit String name;
    @Transmit Server.Type type;
    @Transmit Long uptime;
    @Transmit Double tps;
    @Transmit List<String> players;

    private Server server;

    public ServerUpdatePayload() {
        payloadId = "minecraft-serverUpdate";
    }

    public ServerUpdatePayload(String name, Server.Type type, Long uptime, Double tps, List<String> players) {
        this();
        this.name = name;
        this.type = type;
        this.uptime = uptime;
        this.tps = tps;
        this.players = players;
    }

    @Override
    public void receive() {
        this.server = new Server(this.name, this.type, this.uptime, this.tps, this.players);
    }

}
