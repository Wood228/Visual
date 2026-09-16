package pulsetest;

public final class Setting<T> {
    private final String name;
    private T value;
    private final T min;
    private final T max;
    public Setting(String name, T value) { this(name, value, null, null); }
    public Setting(String name, T value, T min, T max) { this.name = name; this.value = value; this.min = min; this.max = max; }
    public String getName() { return name; }
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }
    public T getMin() { return min; }
    public T getMax() { return max; }
}
