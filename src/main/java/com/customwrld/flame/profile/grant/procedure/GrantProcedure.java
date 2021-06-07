package com.customwrld.flame.profile.grant.procedure;

import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
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
    private final GrantProcedureType type;
    private GrantProcedureStage stage;
    private Grant grant;

    public GrantProcedure(Player issuer, Profile recipient, GrantProcedureType type, GrantProcedureStage stage) {
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
        this.recipient.save();
        this.recipient.reload();

        procedures.remove(this);
    }

    public void cancel() {
        procedures.remove(this);
    }

}