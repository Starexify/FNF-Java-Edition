package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.service.IClassProvider;

import java.net.URL;

public class FunkyClassProvider implements IClassProvider {
    @Override
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return findClass(name, initialize);
    }
}
