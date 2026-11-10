// src/main/java/cheat/core/Module.java
package cheat.core;

public abstract class Module {
    public final String name;
    public final String desc;
    public final Category category;
    public boolean enabled = false;
    public int key = -1;

    public enum Category { COMBAT, MOVEMENT, RENDER, PLAYER, WORLD, MISC }

    public Module(String name, String desc, Category cat) {
        this.name = name;
        this.desc = desc;
        this.category = cat;
    }

    public void toggle() { if (enabled) onDisable(); else onEnable(); enabled = !enabled; }
    public void setEnabled(boolean v) {
        if (v == enabled) return;
        enabled = v;
        if (v) onEnable(); else onDisable();
    }
    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onRender(net.minecraft.client.util.math.MatrixStack ms, float tickDelta) {}
}
