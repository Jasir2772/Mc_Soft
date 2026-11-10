package cheat.mixin;

import cheat.core.ModuleManager;
import cheat.modules.render.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public class BlockRenderMixin {

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void onRender(BlockState state, BlockPos pos, BlockRenderView world,
                          MatrixStack matrices, VertexConsumer vertexConsumer,
                          boolean cull, Random random, CallbackInfo ci) {
        XRay x = ModuleManager.get(XRay.class);
        if (x == null || !x.enabled) return;
        if (!isVisible(state)) ci.cancel();
    }

    private static boolean isVisible(BlockState s) {
        return s.isOf(Blocks.DIAMOND_ORE) || s.isOf(Blocks.DEEPSLATE_DIAMOND_ORE)
            || s.isOf(Blocks.IRON_ORE) || s.isOf(Blocks.DEEPSLATE_IRON_ORE)
            || s.isOf(Blocks.GOLD_ORE) || s.isOf(Blocks.DEEPSLATE_GOLD_ORE)
            || s.isOf(Blocks.EMERALD_ORE) || s.isOf(Blocks.DEEPSLATE_EMERALD_ORE)
            || s.isOf(Blocks.ANCIENT_DEBRIS) || s.isOf(Blocks.CHEST) || s.isOf(Blocks.SPAWNER)
            || s.isOf(Blocks.WATER) || s.isOf(Blocks.LAVA);
    }
}
