package pulsetest;

import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigManager {
    private final Path root;
    private final Path file;
    public ConfigManager() {
        root = MinecraftClient.getInstance().runDirectory.toPath().resolve("PulseTest");
        file = root.resolve("config.json");
    }
    public void save(ModuleManager manager, boolean testMode) {
        try {
            Files.createDirectories(root);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("testMode", testMode);
            Map<String, Boolean> modules = new LinkedHashMap<>();
            for (Module module : manager.all()) modules.put(module.getName(), module.isEnabled());
            data.put("modules", modules);
            Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(data));
        } catch (Exception ignored) {}
    }
    public Path root() { return root; }
}
