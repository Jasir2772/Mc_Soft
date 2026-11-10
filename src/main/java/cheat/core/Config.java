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
                    
                    if (mod.has("settings")) {
                        JsonArray sets = mod.getAsJsonArray("settings");
                        for (int i = 0; i < sets.size(); i++) {
                            JsonObject setObj = sets.get(i).getAsJsonObject();
                            String sName = setObj.get("name").getAsString();
                            for (Setting s : m.settings) {
                                if (s.name.equals(sName)) {
                                    if (s instanceof Setting.BooleanSetting bs) bs.value = setObj.get("val").getAsBoolean();
                                    if (s instanceof Setting.NumberSetting ns) ns.value = setObj.get("val").getAsDouble();
                                    if (s instanceof Setting.ModeSetting ms) ms.index = setObj.get("val").getAsInt();
                                }
                            }
                        }
                    }
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
                
                JsonArray sets = new JsonArray();
                for (Setting s : m.settings) {
                    JsonObject setObj = new JsonObject();
                    setObj.addProperty("name", s.name);
                    if (s instanceof Setting.BooleanSetting bs) setObj.addProperty("val", bs.value);
                    if (s instanceof Setting.NumberSetting ns) setObj.addProperty("val", ns.value);
                    if (s instanceof Setting.ModeSetting ms) setObj.addProperty("val", ms.index);
                    sets.add(setObj);
                }
                mod.add("settings", sets);
                o.add(m.name, mod);
            }
            Files.writeString(FILE, new GsonBuilder().setPrettyPrinting().create().toJson(o));
        } catch (Exception ignored) {}
    }
}
