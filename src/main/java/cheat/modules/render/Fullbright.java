// src/main/java/cheat/modules/render/Fullbright.java
package cheat.modules.render;

import cheat.core.Module;
import net.minecraft.client.MinecraftClient;

public class Fullbright extends Module {
    public Fullbright() { super("Fullbright", "Qorong'ida ko'rish", Category.RENDER); key = org.lwjgl.glfw.GLFW.GLFW_KEY_G; }

    @Override public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) mc.options.getGamma().setValue(10.0);
    }
    @Override public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) mc.options.getGamma().setValue(1.0);
    }
}
