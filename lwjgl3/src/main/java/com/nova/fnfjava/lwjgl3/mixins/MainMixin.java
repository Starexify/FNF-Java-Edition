package com.nova.fnfjava.lwjgl3.mixins;

import com.nova.fnfjava.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void render(CallbackInfo ci) {
        System.out.println("MainMixin render injection !");
    }
}
