package cheat.core;

import cheat.modules.combat.KillAura;
import cheat.modules.combat.Velocity;
import cheat.modules.movement.Speed;
import cheat.modules.movement.Fly;
import cheat.modules.movement.NoFall;
import cheat.modules.movement.NoSlow; // Yangi qo'shildi
import cheat.modules.render.XRay;
import cheat.modules.render.Fullbright;
import cheat.modules.render.ESP;
import cheat.modules.player.AutoTotem;
import cheat.modules.player.Sprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();
    public static final Map<String, Module> byName = new HashMap<>();

    public static void init() {
        // Combat
        add(new KillAura());
        add(new Velocity());
        
        // Movement
        add(new Speed());
        add(new Fly());
        add(new NoFall());
        add(new NoSlow()); // YANGI
        
        // Render
        add(new XRay());
        add(new Fullbright());
        add(new ESP());
        
        // Player
        add(new AutoTotem());
        add(new Sprint());

        // Standart holatda yoniq turadiganlar
        Module fb = byName.get("Fullbright");
        if (fb != null) fb.setEnabled(true);
        
        Module sp = byName.get("Sprint");
        if (sp != null) sp.setEnabled(true);
        
        Module nf = byName.get("NoFall");
        if (nf != null) nf.setEnabled(true);
    }

    private static void add(Module m) { 
        modules.add(m); 
        byName.put(m.name, m); 
    }
    
    public static Module get(String n) { 
        return byName.get(n); 
    }
    
    public static <T extends Module> T get(Class<T> c) {
        for (Module m : modules) {
            if (c.isInstance(m)) return c.cast(m);
        }
        return null;
    }
    
    public static void tickAll() { 
        for (Module m : modules) {
            if (m.enabled) m.onTick(); 
        }
    }
}
