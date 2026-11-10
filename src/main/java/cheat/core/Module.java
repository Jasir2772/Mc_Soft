package cheat.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public final String desc;
    public final Category category;
    public boolean enabled = false;
    public int key = -1;
    
    // YANGI: Modul sozlamalari ro'yxati
    public final List<Setting> settings = new ArrayList<>();

    public enum Category { COMBAT, MOVEMENT, RENDER, PLAYER, WORLD, MISC }

    public Module(String name, String desc, Category cat) {
        this.name = name;
        this.desc = desc;
        this.category = cat;
    }
    
    public void addSetting(Setting s) { settings.add(s); }

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
