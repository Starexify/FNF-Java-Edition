package com.nova.fnfjava.mixin;

import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.ITransformerProvider;

import java.util.Collection;
import java.util.Collections;

public class FunkinTransformerProvider implements ITransformerProvider {
    private final MixinTransformer transformer = new IMixinTransformer();

    @Override
    public Collection<ITransformer> getTransformers() {
        return Collections.singletonList(transformer);
    }

    @Override
    public Collection<ITransformer> getDelegatedTransformers() {
        return Collections.emptyList();
    }

    @Override
    public void addTransformerExclusion(String name) {

    }
}
