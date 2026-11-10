// src/main/java/cheat/modules/combat/KillAura.java
package cheat.modules.combat;

import cheat.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class KillAura extends Module {
    private int cooldown = 0;
    private LivingEntity target = null;
    private int targetTicks = 0;
    public double range = 3.0;
    public int cpsTarget = 8; // inson 6-10 CPS
    public boolean humanAim = true;

    public KillAura() { super("KillAura", "Avtomatik urish — AI-bypass bilan", Category.COMBAT); key = org.lwjgl.glfw.GLFW.GLFW_KEY_R; }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) return;
        if (mc.interactionManager == null) return;

        // target tanlash: eng yaqin, ko'rinadigan, FOV ichida
        if (target == null || !target.isAlive() || targetTicks-- <= 0
                || mc.player.squaredDistanceTo(target) > (range + 0.5) * (range + 0.5)) {
            target = findTarget(mc);
            targetTicks = 20; // target 1 soniyada qayta tanlanadi
        }
        if (target == null) return;

        // humanized rotation
        float[] rot = calcRotation(mc.player, target);
        AntiCheatBypass.sendRotation(mc, rot[0], rot[1]);

        // humanized timing — inson CPS
        if (cooldown-- > 0) return;
        double lambda = 1000.0 / cpsTarget;
        cooldown = (int) AntiCheatBypass.tickJitter((int) Math.round(lambda / 50.0));

        // reach check — 3.0 atrofida, 3.05 dan oshmaydi
        if (mc.player.squaredDistanceTo(target) > range * range) return;

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private LivingEntity findTarget(MinecraftClient mc) {
        LivingEntity best = null;
        double bestD = Double.MAX_VALUE;
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || le == mc.player || !le.isAlive()) continue;
            if (le instanceof PlayerEntity pe && (pe.isCreative() || pe.isSpectator())) continue;
            if (le.isInvisible()) continue;
            if (!Optimizer.shouldRenderEntity(le)) continue;
            double d = mc.player.squaredDistanceTo(le);
            if (d > range * range) continue;
            // FOV check
            if (!inFov(mc.player, le, 120)) continue;
            if (d < bestD) { bestD = d; best = le; }
        }
        return best;
    }

    private boolean inFov(net.minecraft.entity.player.PlayerEntity self, Entity t, float fovDeg) {
        double dx = t.getX() - self.getX();
        double dz = t.getZ() - self.getZ();
        double targetYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        double diff = ((targetYaw - self.getYaw()) % 360 + 540) % 360 - 180;
        return Math.abs(diff) <= fovDeg / 2.0;
    }

    private float[] calcRotation(net.minecraft.entity.player.PlayerEntity self, Entity t) {
        double dx = t.getX() - self.getX();
        double dy = (t.getY() + t.getStandingEyeHeight()) - (self.getY() + self.getStandingEyeHeight());
        double dz = t.getZ() - self.getZ();
        double h = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, h));
        return new float[]{yaw, pitch};
    }
              }
