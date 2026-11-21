package me.plinus.visual;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PlinusVisual implements ClientModInitializer {
    public static final String MOD_ID = "plinusvisuals";

    public static KeyBinding menuKey;
    public static KeyBinding orbitKey;

    public static boolean orbitCameraEnabled = true;
    public static boolean noHurtShakeEnabled = true;
    public static boolean autoLiteMode = true;
    public static float orbitSensitivity = 0.15f;

    public static boolean orbitActive = false;
    public static float yawOffset = 0.0f;
    public static float pitchOffset = 0.0f;
    private static float baseYaw = 0.0f;
    private static float basePitch = 0.0f;
    private static double lastMouseX = 0.0;
    private static double lastMouseY = 0.0;
    private static boolean mouseInitialized = false;

    @Override
    public void onInitializeClient() {
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.plinusvisuals.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.plinusvisuals.main"
        ));

        orbitKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.plinusvisuals.orbit",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.plinusvisuals.main"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(PlinusVisual::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        if (client == null || client.player == null) {
            stopOrbit();
            return;
        }

        while (menuKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new VisualScreen());
            }
        }

        if (!orbitCameraEnabled) {
            stopOrbit();
            return;
        }

        boolean altDown = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
                || orbitKey.isPressed();

        if (altDown) {
            if (!orbitActive) {
                startOrbit(client, client.player.getYaw(), client.player.getPitch());
            }
            updateOrbitFromCursor(client);
        } else if (orbitActive) {
            stopOrbit();
        }
    }

    public static void startOrbit(MinecraftClient client, float playerYaw, float playerPitch) {
        orbitActive = true;
        baseYaw = playerYaw;
        basePitch = playerPitch;
        yawOffset = 0.0f;
        pitchOffset = 0.0f;
        mouseInitialized = false;
        if (client != null && client.mouse != null) {
            lastMouseX = client.mouse.getX();
            lastMouseY = client.mouse.getY();
            mouseInitialized = true;
        }
    }

    private static void updateOrbitFromCursor(MinecraftClient client) {
        if (client.mouse == null) {
            return;
        }
        double curX = client.mouse.getX();
        double curY = client.mouse.getY();
        if (!mouseInitialized) {
            lastMouseX = curX;
            lastMouseY = curY;
            mouseInitialized = true;
            return;
        }
        double dx = curX - lastMouseX;
        double dy = curY - lastMouseY;
        lastMouseX = curX;
        lastMouseY = curY;

        yawOffset += (float) (dx * orbitSensitivity);
        pitchOffset = clampPitch(pitchOffset + (float) (dy * orbitSensitivity));
    }

    public static void stopOrbit() {
        orbitActive = false;
        yawOffset = 0.0f;
        pitchOffset = 0.0f;
        mouseInitialized = false;
    }

    public static float getOrbitYaw(float fallbackYaw) {
        if (!orbitActive || !orbitCameraEnabled) {
            return fallbackYaw;
        }
        return baseYaw + yawOffset;
    }

    public static float getOrbitPitch(float fallbackPitch) {
        if (!orbitActive || !orbitCameraEnabled) {
            return fallbackPitch;
        }
        return clampPitch(basePitch + pitchOffset);
    }

    public static boolean shouldRunLightVisuals(MinecraftClient client) {
        if (!autoLiteMode || client == null) {
            return true;
        }
        return client.getCurrentFps() >= 45;
    }

    private static float clampPitch(float pitch) {
        return Math.max(-89.0f, Math.min(89.0f, pitch));
    }
}
