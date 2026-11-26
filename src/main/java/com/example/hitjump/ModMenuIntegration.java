package com.example.hitjump;

import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public String getModId() {
        return HitJumpMod.MOD_ID;
    }

    @Override
    public Function<Screen, Screen> getConfigScreenFactory() {
        return HitJumpConfigScreen::new;
    }
}
