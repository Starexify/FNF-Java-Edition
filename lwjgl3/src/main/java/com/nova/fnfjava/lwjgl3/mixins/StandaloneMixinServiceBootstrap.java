package com.nova.fnfjava.lwjgl3.mixins;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class StandaloneMixinServiceBootstrap implements IMixinServiceBootstrap {

    public StandaloneMixinServiceBootstrap() {
        System.out.println("StandaloneMixinServiceBootstrap constructor called");
    }

    @Override
    public String getName() {
        System.out.println("StandaloneMixinServiceBootstrap.getName() called");
        return "StandaloneMixin";
    }

    @Override
    public String getServiceClassName() {
        System.out.println("StandaloneMixinServiceBootstrap.getServiceClassName() called");
        // Fixed: Now points to the correct package
        return "com.nova.fnfjava.lwjgl3.mixins.StandaloneMixinService";
    }

    @Override
    public void bootstrap() {
        System.out.println("StandaloneMixinServiceBootstrap.bootstrap() called");
    }
}
