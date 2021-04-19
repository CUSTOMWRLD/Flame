package com.customwrld.flame.util;

import com.customwrld.customlib.util.CC;
import com.customwrld.flame.Flame;
import com.customwrld.flame.profile.Profile;
import com.customwrld.flame.profile.grant.Grant;
import com.customwrld.flame.rank.Rank;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class NameMC {

    private static void isVerified(UUID uuid, Consumer<Boolean> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.namemc.com/server/customwrd.com/likes?profile=" + uuid.toString());
                    Scanner s = new Scanner(url.openStream());
                    consumer.accept(s.nextBoolean());
                } catch (Throwable exception) {
                    exception.printStackTrace();
                    consumer.accept(false);
                }

            }
        }.runTaskAsynchronously(Flame.get());
    }

    public static void verify(Profile profile, Player player) {
        List<Rank> activeRanks = new ArrayList<>();
        Rank verified = Rank.findByName("Iron");

        if (verified == null) {
            return;
        }

        profile.getGrants().forEach(grant -> {
            if (!grant.hasExpired()) {
                activeRanks.add(grant.getRank());
            }
        });

        isVerified(profile.getUuid(), callback -> {
            if (callback) {
                if (!activeRanks.contains(verified)) {
                    profile.getGrants().add(new Grant(UUID.randomUUID(), verified, null, System.currentTimeMillis(), "NameMC Verification", Integer.MAX_VALUE));
                    profile.save();
                    profile.checkGrants();

                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);

                    Arrays.asList(CC.CHAT_BAR,
                            CC.GREEN + "Thank you for liking our NameMC!",
                            CC.GREEN + "You have been rewarded with the Verified Rank!",
                            CC.CHAT_BAR).forEach(player::sendMessage);
                }
            } else {
                if (activeRanks.contains(verified)) {
                    profile.getGrants().forEach(grant -> {
                        if (grant.getRank().equals(verified)) {
                            profile.getGrants().remove(grant);
                            profile.checkGrants();
                            profile.save();
                        }
                    });
                }
            }
        });
    }

}
