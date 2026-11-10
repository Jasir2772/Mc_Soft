// src/main/java/cheat/core/AntiCheatBypass.java
package cheat.core;

import java.util.Random;

/**
 * Inson xulqini taqlid qiluvchi bypass qatlami.
 * - GCD-aligned rotation (mouse sensitivity ga mos)
 * - Poisson-distributed timing (inson keystroke taqsimoti)
 * - Aim error (inson qo'li titraydi)
 * - Micro-jitter (ideal harakat flag beradi)
 */
public class AntiCheatBypass {
    private static final Random rng = new Random();

    // Minecraft default mouse sensitivity 0.5 → f = 0.5*0.6+0.2 = 0.5
    // GCD = f * 0.15 * 8 ≈ 0.15 daraja. Barcha rotation bu songa bo'linishi shart.
    public static final float GCD = 0.15f;

    /** Rotatsiyani GCD ga tekislash — anticheat "humanized mouse" deb o'qiydi */
    public static float alignGcd(float delta) {
        float sens = 0.5f * 0.6f + 0.2f;
        float f = sens * sens * sens * 1.2f;
        float gcd = f * 0.15f * 8.0f;
        return Math.round(delta / gcd) * gcd;
    }

    /** Inson qo'li xatosi — ±3 daraja, Gauss taqsimot */
    public static float humanError() {
        return (float) (rng.nextGaussian() * 1.5);
    }

    /** Poisson interval — inson CPS tabiiy taqsimoti */
    public static long poissonDelay(double lambdaMs) {
        double L = Math.exp(-lambdaMs / 1000.0);
        int k = 0;
        double p = 1.0;
        do { k++; p *= rng.nextDouble(); } while (p > L);
        return (long) (k * 1000.0 / lambdaMs);
    }

    /** 0..1 jitter — paket timing uchun */
    public static long tickJitter(int baseTicks) {
        int j = rng.nextInt(3) - 1; // -1, 0, +1
        return Math.max(1, baseTicks + j);
    }

    /** Pack rotation — camera qimirlamaydi, faqat paket */
    public static void sendRotation(net.minecraft.client.MinecraftClient mc,
                                    float targetYaw, float targetPitch) {
        if (mc.getNetworkHandler() == null || mc.player == null) return;
        float dy = alignGcd(targetYaw - mc.player.getYaw()) + humanError();
        float dp = alignGcd(targetPitch - mc.player.getPitch()) + humanError();
        float newYaw = mc.player.getYaw() + dy;
        float newPitch = Math.max(-90, Math.min(90, mc.player.getPitch() + dp));
        mc.getNetworkHandler().sendPacket(
                new net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround(
                        newYaw, newPitch, mc.player.isOnGround()));
    }
}
