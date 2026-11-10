// src/main/java/cheat/modules/render/XRay.java
package cheat.modules.render;

import cheat.core.Module;
import net.minecraft.client.MinecraftClient;

public class XRay extends Module {
    public XRay() { super("XRay", "Ruda ko'rish", Category.RENDER); key = org.lwjgl.glfw.GLFW.GLFW_KEY_X; }

    @Override public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.worldRenderer != null) mc.worldRenderer.reload();
    }
    @Override public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.worldRenderer != null) mc.worldRenderer.reload();
    }
}
