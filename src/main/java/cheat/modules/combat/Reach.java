// src/main/java/cheat/modules/combat/Reach.java
package cheat.modules.combat;

import cheat.core.Module;

public class Reach extends Module {
    public double reach = 3.05; // vanilla 3.0, 3.05 humanized

    public Reach() { super("Reach", "Urish masofasi", Category.COMBAT); }

    // Ishlatilishi: mixin orqali ClientPlayerInteractionManager.attack reach'i override
    // Keyingi versiyada mixin qo'shaman
}
