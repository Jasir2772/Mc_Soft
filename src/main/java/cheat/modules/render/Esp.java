// src/main/java/cheat/modules/render/ESP.java
package cheat.modules.render;

import cheat.core.Module;
import cheat.core.Optimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.*;

public class ESP extends Module {
    public float r = 1f, g = 0f, b = 0f, a = 0.6f;
    public double maxDist = 32;

    public ESP() { super("ESP", "O'yinchilarni ko'rish", Category.RENDER); }

    @Override public void onRender(MatrixStack ms, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        Camera cam = mc.gameRenderer.getCamera();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || le == mc.player) continue;
            if (!(le instanceof PlayerEntity)) continue; // faqat o'yinchilar — yengil
            if (!Optimizer.shouldRenderEntity(e)) continue;
            if (mc.player.squaredDistanceTo(e) > maxDist * maxDist) continue;
            drawBox(ms, cam, le, tickDelta);
        }
    }

    private void drawBox(MatrixStack ms, Camera cam, Entity e, float td) {
        double x = e.getLerpedPos(td).x - cam.getPos().x;
        double y = e.getLerpedPos(td).y - cam.getPos().y;
        double z = e.getLerpedPos(td).z - cam.getPos().z;
        double w = e.getWidth() / 2.0 + 0.1;
        double h = e.getHeight() + 0.1;

        // pastki va yuqori yuzalar uchun outline
        drawLine(ms, x - w, y, z - w, x + w, y, z - w);
        drawLine(ms, x + w, y, z - w, x + w, y, z + w);
        drawLine(ms, x + w, y, z + w, x - w, y, z + w);
        drawLine(ms, x - w, y, z + w, x - w, y, z - w);
        drawLine(ms, x - w, y + h, z - w, x + w, y + h, z - w);
        drawLine(ms, x + w, y + h, z - w, x + w, y + h, z + w);
        drawLine(ms, x + w, y + h, z + w, x - w, y + h, z + w);
        drawLine(ms, x - w, y + h, z + w, x - w, y + h, z - w);
        // vertikal
        drawLine(ms, x - w, y, z - w, x - w, y + h, z - w);
        drawLine(ms, x + w, y, z - w, x + w, y + h, z - w);
        drawLine(ms, x + w, y, z + w, x + w, y + h, z + w);
        drawLine(ms, x - w, y, z + w, x - w, y + h, z + w);
    }

    private void drawLine(MatrixStack ms, double x1, double y1, double z1, double x2, double y2, double z2) {
        // Implement through RenderLayer.getLines — TODO next version, placeholder no-op
    }
}
