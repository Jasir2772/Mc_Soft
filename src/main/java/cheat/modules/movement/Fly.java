// src/main/java/cheat/modules/movement/Fly.java
package cheat.modules.movement;

import cheat.core.*;
import net.minecraft.client.MinecraftClient;

public class Fly extends Module {
    public double speed = 0.5;
    public double vertical = 0.3;

    public Fly() { super("Fly", "Uchish (packet-safe)", Category.MOVEMENT); key = org.lwjgl.glfw.GLFW.GLFW_KEY_F; }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().allowFlying = true;
        double yaw = Math.toRadians(mc.player.getYaw());
        double fx = 0, fz = 0, fy = 0;
        if (mc.options.forwardKey.isPressed()) { fx -= Math.sin(yaw) * speed; fz += Math.cos(yaw) * speed; }
        if (mc.options.backKey.isPressed()) { fx += Math.sin(yaw) * speed; fz -= Math.cos(yaw) * speed; }
        if (mc.options.leftKey.isPressed()) { fx -= Math.cos(yaw) * speed; fz -= Math.sin(yaw) * speed; }
        if (mc.options.rightKey.isPressed()) { fx += Math.cos(yaw) * speed; fz += Math.sin(yaw) * speed; }
        if (mc.options.jumpKey.isPressed()) fy += vertical;
        if (mc.options.sneakKey.isPressed()) fy -= vertical;
        mc.player.setVelocity(fx, fy, fz);
    }

    @Override public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().allowFlying = mc.player.isCreative();
        }
    }
}
