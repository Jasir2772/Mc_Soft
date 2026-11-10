package cheat.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class AntiCheatBypass {
    private static final Random rng = new Random();
    private static float lastYaw, lastPitch;
    private static int tickCounter = 0;

    // Grim bypass: Rotatsiyani paketga yozish (Silent Aim)
    public static void sendSilentRotation(MinecraftClient mc, float targetYaw, float targetPitch) {
        if (mc.getNetworkHandler() == null || mc.player == null) return;

        // 1. Inson qo'li xatosi (aim error)
        float yawErr = (float) (rng.nextGaussian() * 0.5);
        float pitchErr = (float) (rng.nextGaussian() * 0.5);

        // 2. GCD (Mouse sensitivity) ga moslash
        float f = mc.options.getMouseSensitivity().getValue().floatValue() * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f * 0.15f * 8.0f;

        float finalYaw = Math.round((targetYaw + yawErr) / gcd) * gcd;
        float finalPitch = Math.round((targetPitch + pitchErr) / gcd) * gcd;
        finalPitch = MathHelper.clamp(finalPitch, -90.0f, 90.0f);

        // 3. Mikro-jitter (inson titrashi)
        if (tickCounter++ % 5 == 0) {
            finalYaw += (rng.nextFloat() - 0.5f) * 0.2f;
            finalPitch += (rng.nextFloat() - 0.5f) * 0.1f;
        }

        // 4. Paketni yuborish (Kamera qimirlamaydi, faqat server ko'radi)
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(finalYaw, finalPitch, mc.player.isOnGround()));
        
        // Server tomonda "qo'l silkitish" paketini to'g'ri yuborish
        mc.player.setYaw(finalYaw);
        mc.player.setPitch(finalPitch);
        
        lastYaw = finalYaw;
        lastPitch = finalPitch;
    }

    // Paket jitter (Poisson taqsimoti)
    public static int tickJitter(int baseTicks) {
        int jitter = (int) Math.round(-Math.log(1.0 - rng.nextDouble()) * baseTicks);
        return Math.max(1, baseTicks + (jitter - baseTicks) / 2);
    }
}
