package com.nova.fnfjava.mixin;

import com.nova.fnfjava.TestClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TestClass.class)
public class TestClassMixin {
    @Inject(method = "method", at = @At("HEAD"))
    private static void methodMixed(CallbackInfo ci) {
        System.out.println("This is a TestClassMixin");
    }
}
