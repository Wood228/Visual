package pulsetest;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public final class HudRenderer {
    private HudRenderer() {}
    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        card(ctx, 12, 12, 210, 42);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("Pulse Test"), 26, 21, 0xFFFFFFFF);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal(PulseClient.INSTANCE.testMode ? "TEST MODE" : "READY"), 26, 33, PulseClient.INSTANCE.testMode ? 0xFFB08CFF : 0xFF8C8C9C);
        card(ctx, sw - 205, 12, 193, 74);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("Player"), sw - 193, 21, 0xFFFFFFFF);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal(mc.player.getName().getString()), sw - 193, 34, 0xFFBDB8C8);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("XYZ " + mc.player.getBlockX() + " " + mc.player.getBlockY() + " " + mc.player.getBlockZ()), sw - 193, 48, 0xFFBDB8C8);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("FPS " + mc.getCurrentFps()), sw - 193, 62, 0xFFBDB8C8);
        if (PulseClient.INSTANCE.testMode) {
            card(ctx, sw / 2 - 90, 10, 180, 30);
            String s = "TEST SESSION  " + (PulseClient.INSTANCE.tests.running() ? "RUNNING" : "STOPPED");
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(s), sw / 2 - 77, 20, 0xFFFFFFFF);
        }
        card(ctx, sw - 205, sh - 86, 193, 74);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("Hot Keys"), sw - 193, sh - 74, 0xFFFFFFFF);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("Right Shift  GUI"), sw - 193, sh - 59, 0xFFBDB8C8);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("Insert  Test Mode"), sw - 193, sh - 45, 0xFFBDB8C8);
        ctx.drawTextWithShadow(mc.textRenderer, Text.literal("Right Ctrl  Stop"), sw - 193, sh - 31, 0xFFBDB8C8);
    }
    private static void card(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x + 2, y + 2, x + w + 2, y + h + 2, 0x40000000);
        ctx.fill(x, y, x + w, y + h, 0xE5101018);
        ctx.fill(x, y, x + 2, y + h, 0xFF8D63FF);
    }
}
