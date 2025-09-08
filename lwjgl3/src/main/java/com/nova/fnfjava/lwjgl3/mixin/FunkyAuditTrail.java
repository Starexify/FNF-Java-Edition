package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.service.IMixinAuditTrail;

public class FunkyAuditTrail implements IMixinAuditTrail {
    @Override
    public void onApply(String className, String mixinName) {
        System.out.println("Applied mixin: " + mixinName + " to " + className);
    }

    @Override
    public void onPostProcess(String className) {

    }

    @Override
    public void onGenerate(String className, String generatorName) {

    }
}
