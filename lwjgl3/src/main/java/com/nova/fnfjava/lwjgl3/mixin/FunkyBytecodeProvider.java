package com.nova.fnfjava.lwjgl3.mixin;

import com.nova.fnfjava.lwjgl3.FunkyClassLoader;
import com.nova.fnfjava.lwjgl3.FunkyLoader;
import com.nova.fnfjava.lwjgl3.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.IClassBytecodeProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FunkyBytecodeProvider implements IClassBytecodeProvider {
    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        return getClassNode(name, runTransformers, 0);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers, int readerFlags)
        throws ClassNotFoundException, IOException {
        byte[] classBytes = getClassBytes(name, runTransformers);
        if (classBytes == null) {
            throw new ClassNotFoundException("Could not find class: " + name);
        }

        ClassReader reader = new ClassReader(classBytes);
        ClassNode node = new ClassNode();
        reader.accept(node, readerFlags);
        return node;
    }

    private byte[] getClassBytes(String name, boolean runTransformers) throws IOException {
        FunkyClassLoader classLoader = FunkyLoader.getClassLoader();
        if (classLoader == null) {
            // Fallback to current thread's class loader
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            String resourceName = name.replace('.', '/') + ".class";

            try (InputStream is = cl.getResourceAsStream(resourceName)) {
                if (is == null) {
                    return null;
                }
                return readAllBytes(is);
            }
        }

        byte[] classBytes = classLoader.getClassBytes(name);

        if (runTransformers && classBytes != null) {
            // Apply non-mixin transformers to avoid recursion
            for (IClassTransformer transformer : classLoader.getTransformers()) {
                // Skip mixin transformers to prevent infinite recursion
                if (transformer.getClass().getName().toLowerCase().contains("mixin")) {
                    continue;
                }
                try {
                    classBytes = transformer.transform(name, name, classBytes);
                } catch (Exception e) {
                    System.err.println("Transformer failed for " + name + ": " + e.getMessage());
                }
            }
        }

        return classBytes;
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        return baos.toByteArray();
    }
}
