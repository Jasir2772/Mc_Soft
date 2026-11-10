// src/main/java/cheat/gui/ClickGUI.java
package cheat.gui;

import cheat.core.Module;
import cheat.core.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

import java.util.*;

public class ClickGUI extends Screen {
    private final Map<Module.Category, List<Module>> cats = new LinkedHashMap<>();
    private int scroll = 0;

    public ClickGUI() {
        super(Text.literal("CheatMod Pro"));
        for (Module.Category c : Module.Category.values()) cats.put(c, new ArrayList<>());
        for (Module m : ModuleManager.modules) cats.get(m.category).add(m);
    }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xA0000000);

        int x = 20, y = 40 - scroll;
        for (var e : cats.entrySet()) {
            Module.Category cat = e.getKey();
            List<Module> ms = e.getValue();
            int w = 150;
            ctx.fill(x, y, x + w, y + 18, 0xFF101010);
            ctx.drawText(textRenderer, cat.name(), x + 6, y + 5, 0xFFFFAA00, false);
            y += 20;
            for (Module m : ms) {
                boolean hover = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + 16;
                ctx.fill(x, y, x + w, y + 16, hover ? 0xFF202020 : 0xFF151515);
                if (m.enabled) ctx.fill(x, y, x + 3, y + 16, 0xFF00FF00);
                ctx.drawText(textRenderer, m.name, x + 8, y + 4, m.enabled ? 0xFF00FF00 : 0xFFCCCCCC, false);
                y += 16;
            }
            y += 10;
            if (x + 320 < width) x += 180;
            else { x = 20; y += 180; }
        }
    }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        int x = 20, y = 40 - scroll;
        for (var e : cats.entrySet()) {
            List<Module> ms = e.getValue();
            y += 20;
            for (Module m : ms) {
                if (mx >= x && mx < x + 150 && my >= y && my < y + 16) {
                    m.toggle();
                    return true;
                }
                y += 16;
            }
            y += 10;
            if (x + 320 < width) x += 180;
            else { x = 20; y += 180; }
        }
        return false;
    }

    @Override public boolean mouseScrolled(double mx, double my, double amt) {
        scroll = Math.max(0, scroll - (int)(amt * 20));
        return true;
    }

    @Override public boolean shouldPause() { return false; }
                                        }
