package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FunkyGlobalPropertyService implements IGlobalPropertyService {
    private static final Map<String, Object> properties = new ConcurrentHashMap<>();

    static {
        properties.put("mixin.env", "client");
        properties.put("mixin.hotSwap", false);
        properties.put("mixin.debug", false);
        properties.put("mixin.checks", true);
        properties.put("mixin.dumpsOnFailure", false);
    }

    @Override
    public IPropertyKey resolveKey(String name) {
        return new MixinPropertyKey(name);
    }

    private String keyString(IPropertyKey key) {
        return ((MixinPropertyKey) key).key();
    }

    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) properties.get(keyString(key));
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        properties.put(keyString(key), value);
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) properties.getOrDefault(keyString(key), defaultValue);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        Object o = properties.get(keyString(key));
        return o != null ? o.toString() : defaultValue;
    }

    public static void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public record MixinPropertyKey(String key) implements IPropertyKey {
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MixinPropertyKey)) {
                return false;
            } else {
                return Objects.equals(this.key, ((MixinPropertyKey) obj).key);
            }
        }

        @Override
        public String toString() {
            return this.key;
        }
    }
}
