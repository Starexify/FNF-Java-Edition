package com.nova.fnfjava.mixins;

import com.badlogic.gdx.Game;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Game.class)
public class GameMixin {

    static {
        System.out.println("=== GameMixin class loaded! ===");
    }

    @Inject(method = "render()V", at = @At("HEAD"))
    private void renderMixin(CallbackInfo ci) {
        System.out.println("=== MIXIN RENDER CALLED! ===");
    }
}
