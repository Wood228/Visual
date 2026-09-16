package pulsetest;

import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Test-only combat telemetry recorder. It records the player's own input and
 * camera trajectory; it does not select targets or issue attacks.
 */
public final class CombatBehaviorRecorder {
    public record Sample(long tick, float yaw, float pitch, double x, double y, double z,
                         boolean forward, boolean back, boolean left, boolean right,
                         boolean jump, boolean sprint, boolean attack, boolean use) {}

    private final List<Sample> samples = new ArrayList<>();
    private boolean recording;
    private long tick;

    public void start() {
        samples.clear();
        tick = 0;
        recording = true;
    }

    public void stop() {
        recording = false;
    }

    public boolean isRecording() {
        return recording;
    }

    public int size() {
        return samples.size();
    }

    public void tick() {
        if (!recording) return;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity p = client.player;
        if (p == null) return;

        samples.add(new Sample(
                tick++, p.getYaw(), p.getPitch(), p.getX(), p.getY(), p.getZ(),
                client.options.forwardKey.isPressed(), client.options.backKey.isPressed(),
                client.options.leftKey.isPressed(), client.options.rightKey.isPressed(),
                client.options.jumpKey.isPressed(), client.options.sprintKey.isPressed(),
                client.options.attackKey.isPressed(), client.options.useKey.isPressed()));
    }

    public void export(Path file) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(samples));
    }

    public void clear() {
        samples.clear();
        tick = 0;
    }
}
