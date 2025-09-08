package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.service.IClassTracker;

public class FunkyClassTracker implements IClassTracker {
    @Override
    public void registerInvalidClass(String className) {
    }

    @Override
    public boolean isClassLoaded(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String getClassRestrictions(String className) {
        return "";
    }
}
