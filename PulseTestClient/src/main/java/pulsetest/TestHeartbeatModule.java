package pulsetest;

public final class TestHeartbeatModule extends Module {
    private int ticks;
    public TestHeartbeatModule() { super("Test Heartbeat", "Local telemetry generator for authorized Anti-Cheat testing.", Category.TESTING); }
    @Override public void tick() {
        if (!PulseClient.INSTANCE.testMode || !PulseClient.INSTANCE.tests.running()) return;
        if (++ticks % 40 == 0) PulseClient.INSTANCE.tests.log(getName(), "heartbeat", "periodic test event", "tick=" + ticks, "TEST_EVENT");
    }
    @Override public void onDisable() { ticks = 0; }
}
