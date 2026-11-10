// src/main/java/cheat/modules/movement/Speed.java
package cheat.modules.movement;

import cheat.core.*;
import net.minecraft.client.MinecraftClient;

public class Speed extends Module {
    public double multiplier = 1.15; // vanilla 1.0, 1.15-1.3 humanized

    public Speed() { super("Speed", "Harakat tezligi", Category.MOVEMENT); key = org.lwjgl.glfw.GLFW.GLFW_KEY_V; }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.player.isOnGround() && mc.options.forwardKey.isPressed()) {
            double yaw = Math.toRadians(mc.player.getYaw());
            double fx = -Math.sin(yaw) * 0.1 * multiplier;
            double fz = Math.cos(yaw) * 0.1 * multiplier;
            // jitter — ideal vektor flag beradi
            fx += (Math.random() - 0.5) * 0.01;
            fz += (Math.random() - 0.5) * 0.01;
            mc.player.addVelocity(fx, 0, fz);
        }
    }
}
