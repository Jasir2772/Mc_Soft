package cheat.modules.movement;

import cheat.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoSlow extends Module {
    public NoSlow() { super("NoSlow", "Oziqlanishda sekinlashmaslik", Category.MOVEMENT); }

    @Override public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        
        if (mc.player.isUsingItem()) {
            // Grim bypass: har 3 tickda yerga tegganini aytish
            if (mc.player.age % 3 == 0) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            }
        }
    }
}
