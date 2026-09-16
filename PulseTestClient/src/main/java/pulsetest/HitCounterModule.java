package pulsetest;

public final class HitCounterModule extends Module {
    private int hits;
    public HitCounterModule() { super("Hit Counter", "Local combat event counter for HUD/test telemetry.", Category.HUD); }
    public void registerHit() { hits++; PulseClient.INSTANCE.tests.log("Hit Counter", "hit", "counter increment", Integer.toString(hits), "TEST_EVENT"); }
    public int hits() { return hits; }
    public void reset() { hits = 0; }
}
