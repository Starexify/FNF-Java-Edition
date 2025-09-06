package com.nova.fnfjava.mixins;

import com.nova.fnfjava.TestClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TestClass.class)
public class TestClassMixin {
    static {
        System.out.println("TestClassMixin static block executed!");
    }

    @Inject(method = "testMethod", at = @At("HEAD"))
    private void beforeTestMethod(CallbackInfo ci) {
        System.out.println("=== MIXIN SUCCESS === TestClassMixin intercepted testMethod!");
    }

    @Inject(method = "staticTestMethod", at = @At("HEAD"))
    private static void beforeStaticTestMethod(CallbackInfo ci) {
        System.out.println("=== MIXIN SUCCESS === TestClassMixin intercepted staticTestMethod!");
    }
}
