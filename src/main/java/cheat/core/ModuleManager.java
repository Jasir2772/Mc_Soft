// src/main/java/cheat/core/ModuleManager.java
package cheat.core;

import cheat.modules.combat.*;
import cheat.modules.movement.*;
import cheat.modules.render.*;
import cheat.modules.player.*;
import java.util.*;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();
    public static final Map<String, Module> byName = new HashMap<>();

    public static void init() {
        // COMBAT
        add(new KillAura());
        add(new Velocity());
        add(new Reach());
        // MOVEMENT
        add(new Speed());
        add(new Fly());
        add(new NoFall());
        // RENDER
        add(new XRay());
        add(new Fullbright());
        add(new ESP());
        // PLAYER
        add(new AutoTotem());
        add(new Sprint());

        // default ON (yengil modullar — Pojav-safe)
        byName.get("Fullbright").setEnabled(true);
        byName.get("Sprint").setEnabled(true);
        byName.get("NoFall").setEnabled(true);
    }

    private static void add(Module m) { modules.add(m); byName.put(m.name, m); }

    public static Module get(String n) { return byName.get(n); }
    public static <T extends Module> T get(Class<T> c) {
        for (Module m : modules) if (c.isInstance(m)) return c.cast(m);
        return null;
    }
    public static void tickAll() { for (Module m : modules) if (m.enabled) m.onTick(); }
}
