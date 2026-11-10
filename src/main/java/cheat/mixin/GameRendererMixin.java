package cheat.mixin;

import cheat.core.ModuleManager;
import cheat.modules.render.ESP;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack ms, CallbackInfo ci) {
        ESP esp = ModuleManager.get(ESP.class);
        if (esp != null && esp.enabled) esp.onRender(ms, tickDelta);
    }
}
