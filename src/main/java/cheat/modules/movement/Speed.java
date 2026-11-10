package cheat.modules.movement;

import cheat.core.Module;
import net.minecraft.client.MinecraftClient;

public class Speed extends Module {
    public double multiplier = 1.15;

    public Speed() { super("Speed", "Movement speed", Category.MOVEMENT); key = org.lwjgl.glfw.GLFW.GLFW_KEY_V; }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (mc.player.isOnGround() && mc.options.forwardKey.isPressed()) {
            double yaw = Math.toRadians(mc.player.getYaw());
            double fx = -Math.sin(yaw) * 0.1 * multiplier;
            double fz = Math.cos(yaw) * 0.1 * multiplier;
            fx += (Math.random() - 0.5) * 0.01;
            fz += (Math.random() - 0.5) * 0.01;
            mc.player.addVelocity(fx, 0, fz);
        }
    }
}
