package pulsetest;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;

public final class PulseClient implements ClientModInitializer {
    public static PulseClient INSTANCE;
    public final ModuleManager modules = new ModuleManager();
    public final TestSessionManager tests = new TestSessionManager();
    public final LegitSimulationEngine simulation = new LegitSimulationEngine();
    public final InputSimulationManager input = new InputSimulationManager();
    public final CombatBehaviorRecorder recorder = new CombatBehaviorRecorder();
    public ConfigManager config;
    public boolean testMode;
    private KeyBinding openGui, emergencyStop, toggleTest, startTest, stopTest, recordBehavior, stopRecord;

    @Override public void onInitializeClient() {
        INSTANCE = this; config = new ConfigManager();
        modules.register(new HudInfoModule());
        modules.register(new FullBrightModule());
        modules.register(new HitCounterModule());
        modules.register(new TargetHudModule());
        modules.register(new TestHeartbeatModule());
        openGui = key("key.pulsetest.open_gui", GLFW.GLFW_KEY_RIGHT_SHIFT);
        emergencyStop = key("key.pulsetest.emergency_stop", GLFW.GLFW_KEY_RIGHT_CONTROL);
        toggleTest = key("key.pulsetest.test_mode", GLFW.GLFW_KEY_INSERT);
        startTest = key("key.pulsetest.start_test", GLFW.GLFW_KEY_HOME);
        stopTest = key("key.pulsetest.stop_test", GLFW.GLFW_KEY_END);
        recordBehavior = key("key.pulsetest.record_behavior", GLFW.GLFW_KEY_R);
        stopRecord = key("key.pulsetest.stop_record", GLFW.GLFW_KEY_P);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGui.wasPressed()) client.setScreen(new PulseScreen());
            while (emergencyStop.wasPressed()) emergencyStop();
            while (toggleTest.wasPressed()) testMode = !testMode;
            while (startTest.wasPressed()) startTest();
            while (stopTest.wasPressed()) stopTest();
            while (recordBehavior.wasPressed()) startRecording();
            while (stopRecord.wasPressed()) stopRecording(client);
            if (testMode) recorder.tick();
            modules.tick();
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> HudRenderer.render(drawContext));
    }

    private KeyBinding key(String translationKey, int glfwKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(translationKey, InputUtil.Type.KEYSYM, glfwKey, "category.pulsetest"));
    }

    public void startTest() {
        testMode = true;
        if (!tests.running()) tests.start();
        tests.log("TEST_CENTER", "start", "test session active", "started", "TEST_EVENT");
    }

    public void stopTest() {
        tests.log("TEST_CENTER", "stop", "test session active", "stopped", "TEST_EVENT");
        tests.stop();
        recorder.stop();
        input.clear();
        input.setEnabled(false);
    }

    private void startRecording() {
        if (!testMode) return;
        recorder.start();
        tests.log("BEHAVIOR_RECORDER", "start", "recording enabled in test mode", "recording", "TEST_EVENT");
    }

    private void stopRecording(net.minecraft.client.MinecraftClient client) {
        if (!recorder.isRecording()) return;
        recorder.stop();
        try {
            Path file = client.runDirectory.toPath().resolve("pulsetest/behavior-" + System.currentTimeMillis() + ".json");
            recorder.export(file);
            tests.log("BEHAVIOR_RECORDER", "export", "behavior dataset written", file.toString(), "TEST_EVENT");
        } catch (Exception e) {
            tests.log("BEHAVIOR_RECORDER", "export", "behavior dataset written", e.getClass().getSimpleName(), "TEST_ERROR");
        }
    }

    public void emergencyStop() {
        modules.disableAll();
        tests.stop();
        recorder.stop();
        recorder.clear();
        input.clear();
        input.setEnabled(false);
        testMode = false;
        saveConfig();
    }

    public void saveConfig() { config.save(modules, testMode); }
}
