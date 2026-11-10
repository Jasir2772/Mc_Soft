// src/main/java/cheat/core/Config.java
package cheat.core;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;

public class Config {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("cheatmod.json");

    public static void load() {
        try {
            if (!Files.exists(FILE)) return;
            JsonObject o = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            for (Module m : ModuleManager.modules) {
                if (o.has(m.name)) {
                    JsonObject mod = o.getAsJsonObject(m.name);
                    if (mod.has("enabled")) m.setEnabled(mod.get("enabled").getAsBoolean());
                    if (mod.has("key")) m.key = mod.get("key").getAsInt();
                }
            }
        } catch (Exception ignored) {}
    }

    public static void save() {
        try {
            JsonObject o = new JsonObject();
            for (Module m : ModuleManager.modules) {
                JsonObject mod = new JsonObject();
                mod.addProperty("enabled", m.enabled);
                mod.addProperty("key", m.key);
                o.add(m.name, mod);
            }
            Files.writeString(FILE, new GsonBuilder().setPrettyPrinting().create().toJson(o));
        } catch (Exception ignored) {}
    }
}
