package com.nova.fnfjava.mixins;

import com.badlogic.gdx.Game;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Game.class)
public class GameMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void render(CallbackInfo ci) {
        System.out.println("GameMixin render injection !");
    }
}
