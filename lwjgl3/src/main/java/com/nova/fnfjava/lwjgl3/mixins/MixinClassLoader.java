package com.nova.fnfjava.lwjgl3.mixins;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import java.io.IOException;
import java.io.InputStream;

public class MixinClassLoader extends ClassLoader {
    private final IMixinTransformer transformer;

    public MixinClassLoader(ClassLoader parent, IMixinTransformer transformer) {
        super(parent);
        this.transformer = transformer;
        System.out.println("MixinClassLoader created with transformer: " + transformer);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Let parent handle system classes and already loaded classes
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun.") ||
            name.startsWith("org.spongepowered.") || findLoadedClass(name) != null) {
            return super.loadClass(name, resolve);
        }

        // Check if this is a class we should transform
        if (shouldTransform(name)) {
            synchronized (getClassLoadingLock(name)) {
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    System.out.println("=== TRANSFORMING CLASS === " + name);
                    c = transformAndDefineClass(name);
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }

        return super.loadClass(name, resolve);
    }

    private boolean shouldTransform(String className) {
        // Transform your game classes, but not mixin classes themselves
        return className.startsWith("com.nova.fnfjava") &&
            !className.startsWith("com.nova.fnfjava.lwjgl3.mixins") &&
            !className.contains("$");  // Skip inner classes for now
    }

    private Class<?> transformAndDefineClass(String name) throws ClassNotFoundException {
        try {
            // Load the original class bytes
            byte[] originalBytes = getClassBytes(name);
            if (originalBytes == null) {
                throw new ClassNotFoundException("Could not load class bytes for: " + name);
            }

            // Apply mixin transformations
            byte[] transformedBytes = null;
            if (transformer != null) {
                try {
                    transformedBytes = transformer.transformClass(MixinEnvironment.getCurrentEnvironment(),
                        name, originalBytes);
                    if (transformedBytes != null) {
                        System.out.println("=== TRANSFORMATION SUCCESS === " + name +
                            " (" + originalBytes.length + " -> " + transformedBytes.length + " bytes)");
                    } else {
                        System.out.println("=== NO TRANSFORMATION === " + name + " (no mixins apply)");
                        transformedBytes = originalBytes;
                    }
                } catch (Exception e) {
                    System.err.println("=== TRANSFORMATION FAILED === " + name + ": " + e.getMessage());
                    e.printStackTrace();
                    transformedBytes = originalBytes; // Fall back to original
                }
            } else {
                System.err.println("=== NO TRANSFORMER === " + name);
                transformedBytes = originalBytes;
            }

            // Define the class with transformed bytes
            return defineClass(name, transformedBytes, 0, transformedBytes.length);

        } catch (Exception e) {
            System.err.println("Failed to transform class " + name + ": " + e.getMessage());
            e.printStackTrace();
            throw new ClassNotFoundException("Failed to transform class: " + name, e);
        }
    }

    private byte[] getClassBytes(String className) throws IOException {
        String resourcePath = className.replace('.', '/') + ".class";
        try (InputStream is = getParent().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Class file not found: " + resourcePath);
            }
            return is.readAllBytes();
        }
    }

}
