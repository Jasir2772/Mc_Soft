package cheat.gui;

import cheat.core.Module;
import cheat.core.ModuleManager;
import cheat.core.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ClickGUI extends Screen {
    private final Map<Module.Category, List<Module>> cats = new LinkedHashMap<>();
    private final Set<Module> expanded = new HashSet<>();
    private int scrollY = 0;
    
    // Animatsiya uchun
    private float openAnim = 0f;
    private long lastRender = System.currentTimeMillis();

    public ClickGUI() {
        super(Text.literal("CheatMod Pro"));
        // Kategoriyalarni to'g'ri tartibda joylashtirish
        for (Module.Category c : Module.Category.values()) cats.put(c, new ArrayList<>());
        for (Module m : ModuleManager.modules) {
            if (cats.containsKey(m.category)) {
                cats.get(m.category).add(m);
            }
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Silliq ochilish animatsiyasi
        long now = System.currentTimeMillis();
        float dt = (now - lastRender) / 1000f;
        lastRender = now;
        openAnim = MathHelper.clamp(openAnim + dt * 4f, 0f, 1f);

        // Orqa fonni qoraytirish (Blur o'rniga shaffof qora)
        ctx.fill(0, 0, width, height, (int) (0xA0000000 * openAnim));

        // Ustunlar uchun o'lchamlar
        int colWidth = 140;
        int spacing = 10;
        int totalW = (colWidth + spacing) * cats.size() - spacing;
        int startX = (width - totalW) / 2;
        int startY = 40 - scrollY;

        int i = 0;
        for (var e : cats.entrySet()) {
            Module.Category cat = e.getKey();
            List<Module> ms = e.getValue();
            
            // Ochilish animatsiyasi (pastdan yuqoriga)
            int currentY = startY + (int) ((1f - openAnim) * 50);
            int x = startX + i * (colWidth + spacing);
            
            // Ustun foni (yumaloq burchakli, qoramtir shisha)
            ctx.fill(x, currentY, x + colWidth, currentY + 20, 0xFF1E1E1E);
            ctx.drawTextWithShadow(textRenderer, "§l" + cat.name(), x + 10, currentY + 6, 0xFFFFFF);
            
            // Kategoriya chizig'i
            ctx.fill(x, currentY + 20, x + colWidth, currentY + 21, 0xFF555555);

            int itemY = currentY + 22;
            for (Module m : ms) {
                boolean hover = mouseX >= x && mouseX < x + colWidth && mouseY >= itemY && mouseY < itemY + 16;
                int bgColor = m.enabled ? 0xFF2C2C2C : 0xFF151515;
                if (hover) bgColor = 0xFF3A3A3A;
                
                ctx.fill(x, itemY, x + colWidth, itemY + 16, bgColor);
                
                // Modul nomi
                int textColor = m.enabled ? 0xFF55FF55 : 0xFFAAAAAA;
                ctx.drawTextWithShadow(textRenderer, m.name, x + 8, itemY + 4, textColor);
                
                // Sozlamalar borligini ko'rsatish
                if (!m.settings.isEmpty()) {
                    ctx.drawTextWithShadow(textRenderer, expanded.contains(m) ? "▼" : "▶", x + colWidth - 15, itemY + 4, 0xFFFFAA00);
                }
                
                itemY += 16;

                // Sozlamalar ochilgan bo'lsa
                if (expanded.contains(m)) {
                    for (Setting s : m.settings) {
                        boolean hovS = mouseX >= x && mouseX < x + colWidth && mouseY >= itemY && mouseY < itemY + 14;
                        ctx.fill(x, itemY, x + colWidth, itemY + 14, hovS ? 0xFF404040 : 0xFF1F1F1F);
                        
                        ctx.drawTextWithShadow(textRenderer, "  " + s.name, x + 6, itemY + 3, 0xFFCCCCCC);
                        ctx.drawTextWithShadow(textRenderer, s.getValueString(), x + colWidth - 15 - textRenderer.getWidth(s.getValueString()), itemY + 3, 0xFFFFAA00);
                        
                        itemY += 14;
                    }
                    // Sozlamalar ostidan ajratuvchi chiziq
                    ctx.fill(x, itemY, x + colWidth, itemY + 1, 0xFF333333);
                    itemY += 1;
                }
            }
            // Ustun oxiri
            ctx.fill(x, itemY, x + colWidth, itemY + 2, 0xFF1E1E1E);
            i++;
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int colWidth = 140;
        int spacing = 10;
        int totalW = (colWidth + spacing) * cats.size() - spacing;
        int startX = (width - totalW) / 2;
        int startY = 40 - scrollY;

        int i = 0;
        for (var e : cats.entrySet()) {
            List<Module> ms = e.getValue();
            int x = startX + i * (colWidth + spacing);
            int currentY = startY;
            
            int itemY = currentY + 22;
            for (Module m : ms) {
                if (mx >= x && mx < x + colWidth && my >= itemY && my < itemY + 16) {
                    if (mx >= x + colWidth - 20 && !m.settings.isEmpty()) {
                        // Sozlamalarni ochish/yopish
                        if (expanded.contains(m)) expanded.remove(m);
                        else expanded.add(m);
                    } else {
                        // Modulni yoqish/o'chirish
                        m.toggle();
                        // Agar yoqilsa, ovoz effekti (ixtiyoriy)
                    }
                    return true;
                }
                itemY += 16;

                if (expanded.contains(m)) {
                    for (Setting s : m.settings) {
                        if (mx >= x && mx < x + colWidth && my >= itemY && my < itemY + 14) {
                            s.onClick();
                            return true;
                        }
                        itemY += 14;
                    }
                    itemY += 1;
                }
            }
            i++;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amt) {
        // Ekranni pastga-yuqoriga surish
        scrollY = Math.max(0, scrollY - (int) (amt * 20));
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
