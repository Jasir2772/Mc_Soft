// src/main/java/cheat/core/CheatClient.java
package cheat.core;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CheatClient implements ClientModInitializer {
    public static KeyBinding guiKey;

    @Override public void onInitializeClient() {
        ModuleManager.init();
        Config.load();
        Optimizer.enabled = true;

        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cheatmod.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.cheatmod"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (guiKey.wasPressed()) {
                if (mc.currentScreen == null)
                    mc.setScreen(new cheat.gui.ClickGUI());
            }
            for (Module m : ModuleManager.modules) {
                if (m.key >= 0 && InputUtil.isKeyPressed(mc.getWindow().getHandle(), m.key)) {
                    // edge detect — keybind toggle
                    if (!KeyState.down(m.name)) { m.toggle(); KeyState.mark(m.name); }
                } else {
                    KeyState.up(m.name);
                }
            }
            ModuleManager.tickAll();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(Config::save));
    }
}
