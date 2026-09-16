package pulsetest;

import java.nio.file.Path;

public final class TestSessionManager {
    private TestSession session;
    public void start() { session = new TestSession(); session.start(); }
    public void stop() { if (session != null) session.stop(); }
    public void log(String module, String action, String expected, String actual, String result) { if (session != null && session.isRunning()) session.log(module, action, expected, actual, result); }
    public boolean running() { return session != null && session.isRunning(); }
    public int events() { return session == null ? 0 : session.eventCount(); }
    public void export(Path root) { if (session == null) return; try { session.export(root.resolve("sessions").resolve("session-" + session.getId() + ".json")); } catch (Exception ignored) {} }
}
