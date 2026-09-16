package pulsetest;

import net.minecraft.client.MinecraftClient;

public final class FullBrightModule extends Module {
    private double previous;
    public FullBrightModule() { super("Full Bright", "Client-side brightness helper.", Category.VISUALS); }
    @Override public void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        previous = client.options.getGamma().getValue();
        client.options.getGamma().setValue(16.0);
    }
    @Override public void onDisable() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.options.getGamma().setValue(previous);
    }
}
