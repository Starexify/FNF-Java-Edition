package com.nova.fnfjava.lwjgl3.mixins;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.concurrent.ConcurrentHashMap;

public class StandaloneGlobalPropertyService implements IGlobalPropertyService {
    private final ConcurrentHashMap<IPropertyKey, Object> properties = new ConcurrentHashMap<>();

    public StandaloneGlobalPropertyService() {
        System.out.println("StandaloneGlobalPropertyService constructor called");
    }

    @Override
    public IPropertyKey resolveKey(String name) {
        return new PropertyKey(name);
    }

    @Override
    public <T> T getProperty(IPropertyKey key) {
        @SuppressWarnings("unchecked")
        T value = (T) properties.get(key);
        return value;
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        if (value == null) {
            properties.remove(key);
        } else {
            properties.put(key, value);
        }
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        T value = getProperty(key);
        return value != null ? value : defaultValue;
    }


    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        Object value = getProperty(key);
        return value != null ? value.toString() : defaultValue;
    }

    private static class PropertyKey implements IPropertyKey {
        private final String name;

        public PropertyKey(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PropertyKey && this.name.equals(((PropertyKey) obj).name);
        }
    }
}
