package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class FunkyMixinServiceBootstrap implements IMixinServiceBootstrap {
    @Override
    public String getName() {
        return "FunkyMixin";
    }

    @Override
    public String getServiceClassName() {
        return "com.nova.fnfjava.lwjgl3.mixin.FunkyMixinService";
    }

    @Override
    public void bootstrap() {
    }
}
