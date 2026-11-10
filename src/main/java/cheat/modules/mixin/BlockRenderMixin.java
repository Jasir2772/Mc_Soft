// src/main/java/cheat/mixin/BlockRenderMixin.java
package cheat.mixin;

import cheat.core.ModuleManager;
import cheat.modules.render.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderManager.class)
public class BlockRenderMixin {
    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void onRender(BlockState state, BlockPos pos, BlockRenderView world,
                          MatrixStack ms, VertexConsumer vc, boolean cull,
                          CallbackInfoReturnable<Boolean> cir) {
        XRay x = ModuleManager.get(XRay.class);
        if (x == null || !x.enabled) return;
        if (!isVisible(state)) cir.setReturnValue(false);
    }

    private static boolean isVisible(BlockState s) {
        return s.isOf(Blocks.DIAMOND_ORE) || s.isOf(Blocks.DEEPSLATE_DIAMOND_ORE)
            || s.isOf(Blocks.IRON_ORE) || s.isOf(Blocks.DEEPSLATE_IRON_ORE)
            || s.isOf(Blocks.GOLD_ORE) || s.isOf(Blocks.DEEPSLATE_GOLD_ORE)
            || s.isOf(Blocks.EMERALD_ORE) || s.isOf(Blocks.DEEPSLATE_EMERALD_ORE)
            || s.isOf(Blocks.ANCIENT_DEBRIS)
            || s.isOf(Blocks.CHEST) || s.isOf(Blocks.SPAWNER)
            || s.isOf(Blocks.WATER) || s.isOf(Blocks.LAVA);
    }
}
