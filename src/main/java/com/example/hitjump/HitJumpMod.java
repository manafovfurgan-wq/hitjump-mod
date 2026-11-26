package com.example.hitjump;

import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.Random;

public class HitJumpMod implements ClientModInitializer {
    public static final String MOD_ID = "hitjump";

    private final Random random = new Random();
    private int lastHurtTime = 0;
    private int pendingJumpTicks = -1;

    @Override
    public void onInitializeClient() {
        HitJumpConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        HitJumpConfig config = HitJumpConfig.getInstance();

        if (!config.enabled || client.player == null || client.world == null) {
            lastHurtTime = 0;
            pendingJumpTicks = -1;
            return;
        }

        int currentHurtTime = client.player.hurtTime;

        // A spike in hurtTime indicates the player just received damage
        if (currentHurtTime > lastHurtTime && currentHurtTime > 0) {
            if (rollFrequency(config.jumpFrequency)) {
                if (config.latencyTicks <= 0) {
                    tryJump(client);
                    pendingJumpTicks = -1;
                } else {
                    pendingJumpTicks = config.latencyTicks;
                }
            }
        }
        lastHurtTime = currentHurtTime;

        // Execute delayed jump once the latency countdown finishes
        if (pendingJumpTicks > 0) {
            pendingJumpTicks--;
            if (pendingJumpTicks == 0) {
                tryJump(client);
                pendingJumpTicks = -1;
            }
        }
    }

    private boolean rollFrequency(int frequencyPercent) {
        if (frequencyPercent <= 0) return false;
        if (frequencyPercent >= 100) return true;
        return random.nextInt(100) < frequencyPercent;
    }

    private void tryJump(MinecraftClient client) {
        if (client.player != null && client.player.onGround) {
            client.player.jump();
        }
    }
}
