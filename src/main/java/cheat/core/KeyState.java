// src/main/java/cheat/core/KeyState.java
package cheat.core;
import java.util.*;
public class KeyState {
    private static final Set<String> down = new HashSet<>();
    public static boolean down(String k) { return down.contains(k); }
    public static void mark(String k) { down.add(k); }
    public static void up(String k) { down.remove(k); }
}
