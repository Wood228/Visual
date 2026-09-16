package pulsetest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class TestSession {
    public record Event(long time, String module, String action, String expected, String actual, String result) {}
    private final String id = Long.toString(System.currentTimeMillis());
    private final long startedAt = System.currentTimeMillis();
    private long stoppedAt;
    private boolean running;
    private final List<Event> events = new ArrayList<>();
    public void start() { running = true; log("SESSION", "start", "session running", "started", "TEST_EVENT"); }
    public void stop() { if (!running) return; running = false; stoppedAt = System.currentTimeMillis(); log("SESSION", "stop", "session running", "stopped", "TEST_EVENT"); }
    public void log(String module, String action, String expected, String actual, String result) { events.add(new Event(System.currentTimeMillis(), module, action, expected, actual, result)); }
    public boolean isRunning() { return running; }
    public int eventCount() { return events.size(); }
    public void export(Path file) throws IOException {
        Files.createDirectories(file.getParent());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.writeString(file, gson.toJson(this));
    }
    public String getId() { return id; }
}
