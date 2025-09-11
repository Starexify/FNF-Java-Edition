package com.nova.fnfjava.lwjgl3.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;

import java.util.Objects;

public class FunkyTransformer {
    private final IMixinTransformer transformer;

    public FunkyTransformer(FunkyMixinService service) {
        IMixinTransformerFactory factory = service.getMixinInternal(IMixinTransformerFactory.class);
        this.transformer = Objects.requireNonNull(factory.createTransformer(), "factory may not create a null transformer");
    }

    public IMixinTransformer getTransformer() {
        return this.transformer;
    }

    public boolean transformClass(String name, ClassNode node) {
        boolean ret = this.transformer.transformClass(MixinEnvironment.getEnvironment(MixinEnvironment.Phase.PREINIT), node.name.replace("/", "."), node);
        return ret;
    }
}
