package com.nova.fnfjava.mixin;

import com.badlogic.gdx.utils.ObjectMap;
import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

public class FunkinGlobalPropertyService implements IGlobalPropertyService {
    private final ObjectMap<IPropertyKey, Object> properties = new ObjectMap<>();

    @Override
    public IPropertyKey resolveKey(String name) {
        return new PropertyKey(name);
    }

    @Override
    public <T> T getProperty(IPropertyKey key) {
        return (T) properties.get(key);
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        properties.put(key, value);
    }

    @Override
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        Object val = properties.get(key);
        return val != null ? (T) val : defaultValue;
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        Object val = properties.get(key);
        return val != null ? val.toString() : defaultValue;
    }

    public record PropertyKey(String name) implements IPropertyKey {
        @Override
        public String toString() {
            return name;
        }
    }
}
