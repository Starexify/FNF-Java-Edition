package com.nova.fnfjava.mixin;

import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import java.io.IOException;
import java.io.InputStream;

public class FunkinMixinClassLoader extends ClassLoader {
    private final IMixinTransformer transformer;

    public FunkinMixinClassLoader(ClassLoader parent) {
        super(parent);
        // Initialize the mixin transformer
        this.transformer = new MixinTransformer();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            // If not found, try to load and transform
            return loadAndTransformClass(name);
        }
    }

    private Class<?> loadAndTransformClass(String name) throws ClassNotFoundException {
        String classPath = name.replace('.', '/') + ".class";
        InputStream is = getParent().getResourceAsStream(classPath);

        if (is == null) {
            throw new ClassNotFoundException(name);
        }

        try {
            byte[] classBytes = is.readAllBytes();

            // Apply mixin transformations
            byte[] transformedBytes = transformer.transformClassBytes(name, name, classBytes);
            if (transformedBytes == null) {
                transformedBytes = classBytes;
            }

            return defineClass(name, transformedBytes, 0, transformedBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Failed to read class: " + name, e);
        }
    }
}
