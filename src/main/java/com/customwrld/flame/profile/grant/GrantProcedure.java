package com.customwrld.flame.profile.grant;

import com.customwrld.commonlib.modules.grant.Grant;
import com.customwrld.commonlib.modules.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class GrantProcedure {

    public static final Set<GrantProcedure> procedures = new HashSet<>();

    private final Player issuer;
    private final Profile recipient;
    private final Type type;
    private Stage stage;
    private Grant grant;

    public GrantProcedure(Player issuer, Profile recipient, Type type, Stage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;

        procedures.add(this);
    }

    public static GrantProcedure getByPlayer(Player player) {
        for (GrantProcedure procedure : procedures) {
            if (procedure.issuer.equals(player)) {
                return procedure;
            }
        }

        return null;
    }

    public void finish() {
        this.recipient.update();

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

        REQUIRE_TIME,
        PROVIDE_CUSTOM_TIME,
        REQUIRE_REASON,
        PROVIDE_CUSTOM_REASON,
        REQUIRE_CONFIRMATION

    }

}