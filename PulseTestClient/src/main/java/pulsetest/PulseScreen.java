package pulsetest;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public final class PulseScreen extends Screen {
    private Category selected = Category.VISUALS;
    private final List<ButtonWidget> buttons = new ArrayList<>();
    public PulseScreen() { super(Text.literal("Pulse Test Client")); }
    @Override protected void init() { rebuild(); }
    private void rebuild() {
        for (ButtonWidget button : buttons) remove(button);
        buttons.clear();
        int i = 0;
        for (Category category : Category.values()) {
            int x = 25 + i * 83; if (x > width - 100) break;
            Category c = category;
            ButtonWidget b = ButtonWidget.builder(Text.literal(category.name()), btn -> { selected = c; rebuild(); }).dimensions(x, 36, 78, 20).build();
            addDrawableChild(b); buttons.add(b); i++;
        }
        int col = 0, row = 0;
        for (Module module : PulseClient.INSTANCE.modules.category(selected)) {
            int x = 30 + col * 280, y = 72 + row * 52;
            ButtonWidget b = ButtonWidget.builder(Text.literal((module.isEnabled() ? "● " : "○ ") + module.getName()), btn -> { module.toggle(); PulseClient.INSTANCE.saveConfig(); rebuild(); }).dimensions(x, y, 260, 40).build();
            addDrawableChild(b); buttons.add(b);
            if (++col >= 2) { col = 0; row++; }
        }
        ButtonWidget test = ButtonWidget.builder(Text.literal(PulseClient.INSTANCE.testMode ? "TEST MODE: ON" : "TEST MODE: OFF"), b -> { PulseClient.INSTANCE.testMode = !PulseClient.INSTANCE.testMode; rebuild(); }).dimensions(width - 180, 36, 145, 20).build();
        addDrawableChild(test); buttons.add(test);
    }
    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xC5080810);
        int panelW = Math.min(width - 30, 940), panelH = Math.min(height - 24, 560);
        int px = (width - panelW) / 2, py = (height - panelH) / 2;
        ctx.fill(px + 3, py + 3, px + panelW + 3, py + panelH + 3, 0x50000000);
        ctx.fill(px, py, px + panelW, py + panelH, 0xF20C0C15);
        ctx.fill(px, py, px + panelW, py + 3, 0xFF8D63FF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Pulse Test Client"), px + 25, py + 16, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Visuals / HUD / Utilities / Testing"), px + 25, py + 30, 0xFF8E899B);
        super.render(ctx, mouseX, mouseY, delta);
    }
    @Override public boolean shouldPause() { return false; }
}
