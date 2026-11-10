package cheat.modules.combat;

import cheat.core.Module;
import net.minecraft.client.MinecraftClient;

public class Velocity extends Module {
    public double horizontal = 0.6;
    public double vertical = 0.8;

    public Velocity() { super("Velocity", "Knockback reduce", Category.COMBAT); }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (Math.abs(mc.player.getVelocity().x) > 0.1 || Math.abs(mc.player.getVelocity().z) > 0.1) {
            mc.player.setVelocity(mc.player.getVelocity().x * horizontal, mc.player.getVelocity().y * vertical, mc.player.getVelocity().z * horizontal);
        }
    }
}
