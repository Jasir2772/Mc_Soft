package cheat.core;

public abstract class Setting {
    public String name;
    public Setting(String name) { this.name = name; }
    public abstract String getValueString();
    public abstract void onClick();

    public static class BooleanSetting extends Setting {
        public boolean value;
        public BooleanSetting(String name, boolean value) { super(name); this.value = value; }
        @Override public String getValueString() { return value ? "ON" : "OFF"; }
        @Override public void onClick() { value = !value; }
    }

    public static class NumberSetting extends Setting {
        public double value, min, max, step;
        public NumberSetting(String name, double value, double min, double max, double step) {
            super(name); this.value = value; this.min = min; this.max = max; this.step = step;
        }
        @Override public String getValueString() { return String.format("%.1f", value); }
        @Override public void onClick() {
            value += step;
            if (value > max) value = min; // Aylana boshlaydi
        }
    }

    public static class ModeSetting extends Setting {
        public String[] modes;
        public int index;
        public ModeSetting(String name, String defaultMode, String... modes) {
            super(name); this.modes = modes;
            for (int i = 0; i < modes.length; i++) if (modes[i].equals(defaultMode)) index = i;
        }
        @Override public String getValueString() { return modes[index]; }
        @Override public void onClick() {
            index++;
            if (index >= modes.length) index = 0;
        }
        public boolean is(String mode) { return modes[index].equals(mode); }
    }
}
