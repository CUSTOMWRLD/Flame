package com.customwrld.flame.pigeon;

import com.customwrld.flame.pigeon.payloads.PunishmentBroadcastPayload;
import com.customwrld.flame.pigeon.payloads.RecalculateProfilePayload;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.punishment.Punishment;
import com.customwrld.pigeon.annotations.PayloadHandler;
import com.customwrld.pigeon.annotations.PayloadListener;
import com.customwrld.pigeon.listeners.Listener;

@PayloadListener
public class Listeners implements Listener {

    @PayloadHandler
    public void onPunishmentBroadcast(PunishmentBroadcastPayload payload) {
        Punishment punishment = Punishment.getById(payload.getPunishmentId());

        if (punishment != null) {
            punishment.broadcast(Profile.getByUuid(punishment.getPlayer()));
        }
    }

//    @PayloadHandler
//    public void onGrantAdd(GrantAddPayload payload) {
//        Profile profile = Profile.getProfiles().get(payload.getRecipient());
//
//        if (profile != null) {
//            Grant.loadNewGrant(profile, payload.getGrant());
//        }
//    }
//
//    @PayloadHandler
//    public void onGrantDelete(GrantDeletePayload payload) {
//        Profile profile = Profile.getProfiles().get(payload.getRecipient());
//
//        if (profile != null) {
//            profile.getGrants().removeIf(other -> Objects.equals(other, grant));
//            profile.getGrants().add(grant);
//            profile.save();
//
//            if (grant.hasExpired()) {
//                Grant.expireGrant(profile, grant);
//            } else {
//                Grant.removeGrant(profile, grant);
//            }
//
//        }
//    }

    @PayloadHandler
    public void onRecalculatePlayer(RecalculateProfilePayload payload) {
        Profile profile = Profile.getProfiles().get(payload.getPlayer());

        if (profile != null) {
            profile.reload();
        }
    }

}
