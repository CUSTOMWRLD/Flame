package com.customwrld.flame.profile.punishment;

import com.customwrld.commonlib.modules.profile.Profile;
import com.customwrld.commonlib.modules.punishment.Punishment;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PunishmentProcedure {

    @Getter
    private static final Set<PunishmentProcedure> procedures = new HashSet<>();

    private final Player issuer;
    private final Profile recipient;
    private final Type type;
    private final Stage stage;
    private Punishment punishment;

    public PunishmentProcedure(Player issuer, Profile recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;

        procedures.add(this);
    }

    public static PunishmentProcedure getByPlayer(Player player) {
        for (PunishmentProcedure procedure : procedures) {
            if (procedure.issuer.equals(player)) {
                return procedure;
            }
        }

        return null;
    }

    public void finish() {
        this.recipient.save();
        procedures.remove(this);
    }

    public void cancel() {
        procedures.remove(this);
    }

    public enum Type {

        ADD,
        REMOVE

    }

    public enum Stage {

        REQUIRE_CLICK,
        REQUIRE_TEXT,
        REQUIRE_CONFIRMATION

    }

}
