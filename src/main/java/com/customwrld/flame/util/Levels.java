package com.customwrld.flame.util;

import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.awt.*;

public class Levels {
    public static final String EXP_SUFFIX = "EXP";
//    public static final ComponentFormatting EXP_FORMAT = ComponentFormatting.builder().color(ChatColor.AQUA).build();
//    public static final ComponentFormatting LEVELS_FORMAT = EXP_FORMAT;
    private static final SimplexNoiseGenerator noiseGenerator = new SimplexNoiseGenerator(3371012348295728621L);

    public static String getLevelAffix(int amount) {
        return "level" + (amount > 1 ? "s" : "");
    }

    public static int getLevelFromExperience(long experience) {
        return (int) Math.floor(Math.pow(experience / 300.0, 1 / 2.0));
    }

    public static long getExperienceFromLevel(int level) {
        return (long) (Math.pow(level, 2) * 300);
    }

    public static Color getLevelColor(int level) {
        float h = (((float) noiseGenerator.noise(level / 20.0)) + 1f) / 2f;
        float s = 0.5f + (((((float) noiseGenerator.noise(0, level / 5.0)) + 1f) / 2f) / 2f);
        float b = 0.5f + (((((float) noiseGenerator.noise(0, 0, level / 5.0)) + 1f) / 2f) / 2f);
        return Color.getHSBColor(h, s, b);
    }
}
