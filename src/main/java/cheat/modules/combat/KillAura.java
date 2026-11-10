package cheat.modules.combat;

import cheat.core.Module;
import cheat.core.Setting;
import cheat.core.AntiCheatBypass;
import cheat.core.Optimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {
    private int cooldown = 0;
    public LivingEntity target = null;
    private int switchTimer = 0;

    public Setting.ModeSetting targetMode;
    public Setting.ModeSetting rotationMode; // Silent yoki Legit
    public Setting.NumberSetting range;
    public Setting.NumberSetting cps;
    public Setting.NumberSetting fov;
    public Setting.BooleanSetting onlyCrit;
    public Setting.BooleanSetting multiTask; // Bir vaqtda bir nechta nishon
    public Setting.BooleanSetting targetEsp;

    public KillAura() {
        super("KillAura", "Anarchy Bypass Aura", Category.COMBAT);
        key = org.lwjgl.glfw.GLFW.GLFW_KEY_R;
        
        targetMode = new Setting.ModeSetting("Target", "Players", "Players", "Mobs", "Both");
        rotationMode = new Setting.ModeSetting("Rotate", "Silent", "Silent", "Legit");
        range = new Setting.NumberSetting("Range", 3.1, 2.5, 6.0, 0.1);
        cps = new Setting.NumberSetting("CPS", 9, 1, 20, 1);
        fov = new Setting.NumberSetting("FOV", 120, 30, 360, 10);
        onlyCrit = new Setting.BooleanSetting("Only Crit", false);
        multiTask = new Setting.BooleanSetting("Multi Task", false);
        targetEsp = new Setting.BooleanSetting("Target ESP", true);
        
        addSetting(targetMode); addSetting(rotationMode); addSetting(range);
        addSetting(cps); addSetting(fov); addSetting(onlyCrit);
        addSetting(multiTask); addSetting(targetEsp);
    }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) return;

        if (switchTimer > 0) switchTimer--;

        if (target == null || !target.isAlive() || mc.player.squaredDistanceTo(target) > (range.value + 0.5) * (range.value + 0.5) || switchTimer <= 0) {
            target = findTarget(mc);
            switchTimer = 10 + (int)(Math.random() * 10); // Har 0.5-1 soniyada nishonni almashtiradi (inson kabi)
        }
        if (target == null) return;

        if (onlyCrit.value && !canCrit(mc)) return;

        // Rotatsiya yuborish
        float[] rot = calcRotation(mc.player, target);
        if (rotationMode.is("Silent")) {
            AntiCheatBypass.sendSilentRotation(mc, rot[0], rot[1]);
        } else {
            // Legit mode (kamera ham buramaydi)
            mc.player.setYaw(rot[0]);
            mc.player.setPitch(rot[1]);
        }

        if (cooldown-- > 0) return;
        double lambda = 1000.0 / cps.value;
        cooldown = AntiCheatBypass.tickJitter((int) Math.round(lambda / 50.0));

        if (mc.player.squaredDistanceTo(target) > range.value * range.value) return;

        // Multi task (qo'shnilarni ham urish)
        List<LivingEntity> targets = new ArrayList<>();
        targets.add(target);
        if (multiTask.value) {
            for (Entity e : mc.world.getEntities()) {
                if (e instanceof LivingEntity le && le != target && le != mc.player && le.isAlive() && mc.player.squaredDistanceTo(le) <= range.value * range.value) {
                    if (targetMode.is("Players") && !(le instanceof PlayerEntity)) continue;
                    if (targetMode.is("Mobs") && !(le instanceof MobEntity)) continue;
                    targets.add(le);
                }
            }
        }

        for (LivingEntity t : targets) {
            mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(t, mc.player.isSneaking()));
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private boolean canCrit(MinecraftClient mc) {
        return mc.player.fallDistance > 0.0F && !mc.player.isOnGround() && !mc.player.isClimbing() && !mc.player.isTouchingWater();
    }

    private LivingEntity findTarget(MinecraftClient mc) {
        List<LivingEntity> valid = new ArrayList<>();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || le == mc.player || !le.isAlive()) continue;
            if (targetMode.is("Players") && !(le instanceof PlayerEntity)) continue;
            if (targetMode.is("Mobs") && !(le instanceof MobEntity)) continue;
            if (le instanceof PlayerEntity pe && (pe.isCreative() || pe.isSpectator())) continue;
            if (le.isInvisible() || !Optimizer.shouldRenderEntity(le)) continue;
            
            double d = mc.player.squaredDistanceTo(le);
            if (d > range.value * range.value) continue;
            
            double dx = le.getX() - mc.player.getX();
            double dz = le.getZ() - mc.player.getZ();
            double targetYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
            double diff = ((targetYaw - mc.player.getYaw()) % 360 + 540) % 360 - 180;
            if (Math.abs(diff) > (fov.value / 2.0)) continue;
            valid.add(le);
        }
        
        if (valid.isEmpty()) return null;
        // Eng yaqinni tanlash
        valid.sort(Comparator.comparingDouble(mc.player::squaredDistanceTo));
        return valid.get(0);
    }

    private float[] calcRotation(PlayerEntity self, Entity t) {
        double dx = t.getX() - self.getX();
        double dy = (t.getY() + t.getStandingEyeHeight()) - (self.getY() + self.getStandingEyeHeight());
        double dz = t.getZ() - self.getZ();
        double h = Math.sqrt(dx * dx + dz * dz);
        return new float[]{(float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0), (float) -Math.toDegrees(Math.atan2(dy, h))};
    }
                }
