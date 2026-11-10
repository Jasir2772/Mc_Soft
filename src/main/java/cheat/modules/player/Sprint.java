// src/main/java/cheat/modules/player/Sprint.java
package cheat.modules.player;

import cheat.core.Module;
import net.minecraft.client.MinecraftClient;

public class Sprint extends Module {
    public Sprint() { super("Sprint", "Doim sprint", Category.PLAYER); }
    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.options.forwardKey.isPressed()) mc.player.setSprinting(true);
    }
}
