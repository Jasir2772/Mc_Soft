package cheat.gui;

import cheat.core.Module;
import cheat.core.ModuleManager;
import cheat.core.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;

public class ClickGUI extends Screen {
    private final Map<Module.Category, List<Module>> cats = new LinkedHashMap<>();
    private final Set<Module> expanded = new HashSet<>();
    private int scroll = 0;

    public ClickGUI() {
        super(Text.literal("CheatMod Pro"));
        for (Module.Category c : Module.Category.values()) cats.put(c, new ArrayList<>());
        for (Module m : ModuleManager.modules) cats.get(m.category).add(m);
    }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xA0000000);
        int x = 20, y = 40 - scroll;
        int maxColumnHeight = 0;

        for (var e : cats.entrySet()) {
            Module.Category cat = e.getKey();
            List<Module> ms = e.getValue();
            int w = 180;
            int startY = y;
            
            ctx.fill(x, y, x + w, y + 20, 0xFF1A1A1A);
            ctx.drawText(textRenderer, "§6" + cat.name(), x + 8, y + 6, 0xFFFFFF, false);
            y += 22;
            
            for (Module m : ms) {
                boolean hover = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + 18;
                ctx.fill(x, y, x + w, y + 18, hover ? 0xFF303030 : 0xFF202020);
                if (m.enabled) ctx.fill(x, y, x + 3, y + 18, 0xFF00FF00);
                
                ctx.drawText(textRenderer, m.name, x + 10, y + 5, m.enabled ? 0xFF55FF55 : 0xFFAAAAAA, false);
                
                if (!m.settings.isEmpty()) {
                    ctx.drawText(textRenderer, expanded.contains(m) ? "v" : ">", x + w - 15, y + 5, 0xFFFFAA00, false);
                }
                y += 20;

                if (expanded.contains(m)) {
                    for (Setting s : m.settings) {
                        boolean hovS = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + 15;
                        ctx.fill(x + 5, y, x + w, y + 15, hovS ? 0xFF404040 : 0xFF252525);
                        ctx.drawText(textRenderer, "  " + s.name, x + 10, y + 3, 0xFFCCCCCC, false);
                        ctx.drawText(textRenderer, s.getValueString(), x + w - 30, y + 3, 0xFFFFAA00, false);
                        y += 16;
                    }
                }
            }
            y += 15;
            maxColumnHeight = Math.max(maxColumnHeight, y - startY);
            
            if (x + 200 < width) x += 200;
            else { x = 20; y += maxColumnHeight + 10; maxColumnHeight = 0; }
        }
    }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        int x = 20, y = 40 - scroll;
        int maxColumnHeight = 0;

        for (var e : cats.entrySet()) {
            List<Module> ms = e.getValue();
            int w = 180;
            int startY = y;
            y += 22;
            
            for (Module m : ms) {
                if (mx >= x && mx < x + w && my >= y && my < y + 18) {
                    if (mx >= x + w - 20) { // Kengaytirish tugmasi
                        if (expanded.contains(m)) expanded.remove(m); else expanded.add(m);
                    } else {
                        m.toggle();
                    }
                    return true;
                }
                y += 20;

                if (expanded.contains(m)) {
                    for (Setting s : m.settings) {
                        if (mx >= x && mx < x + w && my >= y && my < y + 15) {
                            s.onClick();
                            return true;
                        }
                        y += 16;
                    }
                }
            }
            y += 15;
            maxColumnHeight = Math.max(maxColumnHeight, y - startY);
            
            if (x + 200 < width) x += 200;
            else { x = 20; y += maxColumnHeight + 10; maxColumnHeight = 0; }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override public boolean mouseScrolled(double mx, double my, double amt) {
        scroll = Math.max(0, scroll - (int)(amt * 20));
        return true;
    }

    @Override public boolean shouldPause() { return false; }
}
