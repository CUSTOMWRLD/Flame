package com.customwrld.flame.network;

import com.customwrld.customlib.CustomLib;
import com.customwrld.flame.pigeon.payloads.network.GamemodeRequestPayload;
import com.customwrld.flame.pigeon.payloads.network.RegisterGamemodePayload;
import com.customwrld.flame.util.RecordValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@AllArgsConstructor
public class Gamemode {

    @Getter private static final List<Gamemode> cachedGamemodes = Collections.synchronizedList(new ArrayList<>());

    @Getter private final String name;

    @Getter @Setter private int totalPlayers;
    @Getter @Setter private RecordValue totalPlayersRecord;

    public Gamemode(String name) {
        this.name = name;
        totalPlayersRecord = new RecordValue(new Date(), "0");
    }

    public static void getByName(String name, @NotNull Consumer<Gamemode> callback) {
        var cachedGamemode = cachedGamemodes.stream()
                .filter(gamemode -> gamemode.getName().equals(name))
                .findFirst().orElse(null);

        if(cachedGamemode != null) {
            callback.accept(cachedGamemode);
            return;
        }

        var pigeon = CustomLib.getCustomLib().getPigeon();
        pigeon.broadcast(new GamemodeRequestPayload(name, payload -> {
            var gamemode = payload.getGamemode();
            cachedGamemodes.add(gamemode);

            callback.accept(gamemode);
        }));
    }

    public static void register(Gamemode gamemode, @Nullable Consumer<Boolean> callback) {
        getByName(gamemode.getName(), otherGamemode -> {
            if(otherGamemode == null) {
                cachedGamemodes.add(gamemode);

                if(callback != null) { callback.accept(true); }

                var pigeon = CustomLib.getCustomLib().getPigeon();
                pigeon.broadcast(new RegisterGamemodePayload(gamemode));
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Gamemode gamemode)) {
            return false;
        }

        return Objects.equals(name, gamemode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
