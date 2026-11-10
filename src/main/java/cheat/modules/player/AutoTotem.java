// src/main/java/cheat/modules/player/AutoTotem.java
package cheat.modules.player;

import cheat.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class AutoTotem extends Module {
    public AutoTotem() { super("AutoTotem", "Totem avtomatik qo'lda", Category.PLAYER); key = org.lwjgl.glfw.GLFW.GLFW_KEY_T; }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.interactionManager == null) return;
        if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                mc.interactionManager.pickFromInventory(i);
                return;
            }
        }
    }
}
