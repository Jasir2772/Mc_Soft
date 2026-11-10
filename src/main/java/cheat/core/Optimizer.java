// src/main/java/cheat/core/Optimizer.java
package cheat.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class Optimizer {
    public static boolean enabled = true;
    public static int maxEntityDistance = 32;
    public static boolean cullParticles = true;

    public static boolean shouldRenderEntity(Entity e) {
        if (!enabled) return true;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return true;
        double d = mc.player.squaredDistanceTo(e);
        return d <= (double) maxEntityDistance * maxEntityDistance;
    }
}
