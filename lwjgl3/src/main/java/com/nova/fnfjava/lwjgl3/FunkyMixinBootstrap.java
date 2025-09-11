package com.nova.fnfjava.lwjgl3;

import com.nova.fnfjava.lwjgl3.mixin.FunkyMixinService;
import com.nova.fnfjava.lwjgl3.mixin.FunkyMixinServiceBootstrap;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public final class FunkyMixinBootstrap {
    private static boolean initialized = false;

    public static void init() {
        if (initialized) throw new RuntimeException("FunkyMixinBootstrap has already been initialized!");

        System.setProperty("mixin.bootstrapService", FunkyMixinServiceBootstrap.class.getName());
        System.setProperty("mixin.service", FunkyMixinService.class.getName());

        MixinBootstrap.init();
        Mixins.addConfiguration("fnfjava.mixins.json");

        MixinBootstrap.getPlatform().inject();
    }
}
