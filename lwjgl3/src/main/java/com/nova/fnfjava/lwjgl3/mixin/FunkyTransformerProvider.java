package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.service.ITransformer;
import org.spongepowered.asm.service.ITransformerProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FunkyTransformerProvider implements ITransformerProvider {
    private List<ITransformer> transformers = new ArrayList<>();

    @Override
    public Collection<ITransformer> getTransformers() {
        return transformers;
    }

    @Override
    public Collection<ITransformer> getDelegatedTransformers() {
        return Collections.emptyList();
    }

    @Override
    public void addTransformerExclusion(String name) {

    }
}
