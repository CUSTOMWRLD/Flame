package com.customwrld.flame.pigeon;

import com.customwrld.flame.pigeon.payloads.GrantAddPayload;
import com.customwrld.flame.pigeon.payloads.GrantDeletePayload;
import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.pigeon.payloads.RecalculateProfilePayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.pigeon.annotations.PayloadHandler;
import com.customwrld.pigeon.annotations.PayloadListener;
import com.customwrld.pigeon.listeners.Listener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

@PayloadListener
public class Listeners implements Listener {

    @PayloadHandler
    public void onPunishmentBroadcast(PunishmentBroadcastPayload payload) {
        payload.getPunishment().broadcast(payload.getSilent(), Profile.getByUuid(payload.getOffender()));
    }

    @PayloadHandler
    public void onGrantAdd(GrantAddPayload payload) {
        Player player = Bukkit.getPlayer(payload.getRecipient());
        Grant grant = payload.getGrant();

        if (player != null) {
            Profile profile = Profile.profiles.get(player.getUniqueId());
            Grant.loadNewGrant(profile, player, grant);
        }
    }

    @PayloadHandler
    public void onGrantDelete(GrantDeletePayload payload) {
        Player player = Bukkit.getPlayer(payload.getRecipient());
        Grant grant = payload.getGrant();

        if (player != null) {
            Profile profile = Profile.profiles.get(player.getUniqueId());
            profile.getGrants().removeIf(other -> Objects.equals(other, grant));
            profile.getGrants().add(grant);
            profile.save();
            if (grant.hasExpired()) {
                Grant.expireGrant(player, grant);
            } else {
                Grant.removeGrant(player, grant);
            }

        }
    }

    @PayloadHandler
    public void onRecalculatePlayer(RecalculateProfilePayload payload) {
        Profile profile = Profile.profiles.get(payload.getPlayer());

        if(profile != null) {
            profile.reload();
        }
    }

}
