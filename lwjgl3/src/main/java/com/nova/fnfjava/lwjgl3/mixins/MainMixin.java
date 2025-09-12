package com.nova.fnfjava.lwjgl3.mixins;

import com.nova.fnfjava.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainMixin {
    @Inject(method = "create", at = @At("HEAD"))
    private static void initMixin(CallbackInfo ci) {
        System.out.println("MainMixin injected into create !");
    }
}
