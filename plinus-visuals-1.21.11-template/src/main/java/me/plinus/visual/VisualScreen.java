package me.plinus.visual;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class VisualScreen extends Screen {
    public VisualScreen() {
        super(Text.literal("plinus visuals"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;
        int btnWidth = 220;
        int btnHeight = 20;
        int step = 24;

        this.addDrawableChild(ButtonWidget.builder(
                formatToggle("Orbit Camera (Left Alt)", PlinusVisual.orbitCameraEnabled),
                button -> {
                    PlinusVisual.orbitCameraEnabled = !PlinusVisual.orbitCameraEnabled;
                    if (!PlinusVisual.orbitCameraEnabled) {
                        PlinusVisual.stopOrbit();
                    }
                    button.setMessage(formatToggle("Orbit Camera (Left Alt)", PlinusVisual.orbitCameraEnabled));
                }
        ).dimensions(centerX - btnWidth / 2, startY, btnWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                formatToggle("No Hurt Shake", PlinusVisual.noHurtShakeEnabled),
                button -> {
                    PlinusVisual.noHurtShakeEnabled = !PlinusVisual.noHurtShakeEnabled;
                    button.setMessage(formatToggle("No Hurt Shake", PlinusVisual.noHurtShakeEnabled));
                }
        ).dimensions(centerX - btnWidth / 2, startY + step, btnWidth, btnHeight).build());

        this.addDrawableChild(ButtonWidget.builder(
                formatToggle("Auto-Lite Optimization", PlinusVisual.autoLiteMode),
                button -> {
                    PlinusVisual.autoLiteMode = !PlinusVisual.autoLiteMode;
                    button.setMessage(formatToggle("Auto-Lite Optimization", PlinusVisual.autoLiteMode));
                }
        ).dimensions(centerX - btnWidth / 2, startY + step * 2, btnWidth, btnHeight).build());

        this.addDrawableChild(new SliderWidget(
                centerX - btnWidth / 2,
                startY + step * 3,
                btnWidth,
                btnHeight,
                Text.literal(String.format("Orbit Sensitivity: %.2f", PlinusVisual.orbitSensitivity)),
                clampSlider((PlinusVisual.orbitSensitivity - 0.05f) / 0.45f)
        ) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal(String.format("Orbit Sensitivity: %.2f", PlinusVisual.orbitSensitivity)));
            }

            @Override
            protected void applyValue() {
                PlinusVisual.orbitSensitivity = 0.05f + (float) (this.value * 0.45f);
            }
        });

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Close"),
                button -> this.close()
        ).dimensions(centerX - 60, startY + step * 4 + 6, 120, btnHeight).build());
    }

    private double clampSlider(float value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private Text formatToggle(String label, boolean enabled) {
        return Text.literal(label + ": " + (enabled ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                this.height / 4 - 22,
                0xFFFFFF
        );
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
