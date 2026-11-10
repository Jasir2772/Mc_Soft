package cheat.modules.combat;

import cheat.core.Module;
import cheat.core.Setting;
import cheat.core.AntiCheatBypass;
import cheat.core.Optimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {
    private int cooldown = 0;
    public LivingEntity target = null;
    
    public Setting.ModeSetting targetMode;
    public Setting.NumberSetting range;
    public Setting.NumberSetting cps;
    public Setting.NumberSetting fov;
    public Setting.BooleanSetting onlyCrit;
    public Setting.BooleanSetting targetEsp;

    public KillAura() {
        super("KillAura", "Auto hit", Category.COMBAT);
        key = org.lwjgl.glfw.GLFW.GLFW_KEY_R;
        
        targetMode = new Setting.ModeSetting("Target", "Players", "Players", "Mobs", "Both");
        range = new Setting.NumberSetting("Range", 3.0, 2.5, 6.0, 0.1);
        cps = new Setting.NumberSetting("CPS", 8, 1, 20, 1);
        fov = new Setting.NumberSetting("FOV", 120, 30, 360, 10);
        onlyCrit = new Setting.BooleanSetting("Only Crit", false);
        targetEsp = new Setting.BooleanSetting("Target ESP", true);
        
        addSetting(targetMode);
        addSetting(range);
        addSetting(cps);
        addSetting(fov);
        addSetting(onlyCrit);
        addSetting(targetEsp);
    }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) return;

        if (target == null || !target.isAlive() || mc.player.squaredDistanceTo(target) > (range.value + 0.5) * (range.value + 0.5)) {
            target = findTarget(mc);
        }
        if (target == null) return;

        // Only Crit tekshiruvi
        if (onlyCrit.value && !canCrit(mc)) return;

        float[] rot = calcRotation(mc.player, target);
        AntiCheatBypass.sendRotation(mc, rot[0], rot[1]);

        if (cooldown-- > 0) return;
        double lambda = 1000.0 / cps.value;
        cooldown = (int) AntiCheatBypass.tickJitter((int) Math.round(lambda / 50.0));

        if (mc.player.squaredDistanceTo(target) > range.value * range.value) return;

        mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    @Override public void onRender(net.minecraft.client.util.math.MatrixStack ms, float tickDelta) {
        if (targetEsp.value && target != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            Camera cam = mc.gameRenderer.getCamera();
            Vec3d pos = target.getLerpedPos(tickDelta);
            double x = pos.x - cam.getPos().x;
            double y = pos.y - cam.getPos().y;
            double z = pos.z - cam.getPos().z;
            
            Box box = new Box(x - 0.4, y, z - 0.4, x + 0.4, y + target.getHeight(), z + 0.4);
            VertexConsumer vc = mc.getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getLines());
            WorldRenderer.drawBox(ms, vc, box, 1f, 0f, 0f, 1f);
            mc.getBufferBuilders().getEntityVertexConsumers().draw();
        }
    }

    private boolean canCrit(MinecraftClient mc) {
        return mc.player.fallDistance > 0.0F && !mc.player.isOnGround() && !mc.player.isClimbing() && !mc.player.isTouchingWater() && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS) && !mc.player.hasVehicle();
    }

    private LivingEntity findTarget(MinecraftClient mc) {
        LivingEntity best = null;
        double bestD = Double.MAX_VALUE;
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || le == mc.player || !le.isAlive()) continue;
            
            // Target Mode tekshiruvi
            if (targetMode.is("Players") && !(le instanceof PlayerEntity)) continue;
            if (targetMode.is("Mobs") && !(le instanceof MobEntity)) continue;
            if (le instanceof PlayerEntity pe && (pe.isCreative() || pe.isSpectator())) continue;

            if (le.isInvisible() || !Optimizer.shouldRenderEntity(le)) continue;
            double d = mc.player.squaredDistanceTo(le);
            if (d > range.value * range.value) continue;
            
            // FOV tekshiruvi
            double dx = le.getX() - mc.player.getX();
            double dz = le.getZ() - mc.player.getZ();
            double targetYaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
            double diff = ((targetYaw - mc.player.getYaw()) % 360 + 540) % 360 - 180;
            if (Math.abs(diff) > (fov.value / 2.0)) continue;

            if (d < bestD) { bestD = d; best = le; }
        }
        return best;
    }

    private float[] calcRotation(PlayerEntity self, Entity t) {
        double dx = t.getX() - self.getX();
        double dy = (t.getY() + t.getStandingEyeHeight()) - (self.getY() + self.getStandingEyeHeight());
        double dz = t.getZ() - self.getZ();
        double h = Math.sqrt(dx * dx + dz * dz);
        return new float[]{(float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0), (float) -Math.toDegrees(Math.atan2(dy, h))};
    }
            }
