package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FunkyGlobalPropertyService implements IGlobalPropertyService {
    private final Map<String, IPropertyKey> keys = new HashMap<>();
    private final Map<IPropertyKey, Object> values = new HashMap<>();

    @Override
    public IPropertyKey resolveKey(String name) {
        IPropertyKey key = this.keys.get(name);

        if (key == null) {
            key = new MixinPropertyKey(name);
            this.keys.put(name, key);
        }

        return key;
    }

    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) this.values.get(key);
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        this.values.put(key, value);
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) this.values.getOrDefault(key, defaultValue);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return (String) this.values.getOrDefault(key, defaultValue);
    }

    public record MixinPropertyKey(String name) implements IPropertyKey {
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MixinPropertyKey)) return false;

            return Objects.equals(this.name, ((MixinPropertyKey) o).name);
        }

        @Override
        public String toString() {
            return "MixinPropertyKey{key='" + name + '\'' + '}';
        }
    }
}
