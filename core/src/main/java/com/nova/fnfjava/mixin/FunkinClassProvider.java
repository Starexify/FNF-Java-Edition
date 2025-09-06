package com.nova.fnfjava.mixin;

import org.spongepowered.asm.service.IClassProvider;

import java.net.URL;

public class FunkinClassProvider implements IClassProvider {
    private final ClassLoader classLoader;

    public FunkinClassProvider(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    @Deprecated
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }
}
