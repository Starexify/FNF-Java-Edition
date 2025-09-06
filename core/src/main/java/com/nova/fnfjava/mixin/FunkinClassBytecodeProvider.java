package com.nova.fnfjava.mixin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.IClassBytecodeProvider;

import java.io.IOException;
import java.io.InputStream;

public class FunkinClassBytecodeProvider implements IClassBytecodeProvider {
    private final ClassLoader classLoader;

    public FunkinClassBytecodeProvider(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        String path = name.replace('.', '/') + ".class";
        try (InputStream is = classLoader.getResourceAsStream(path)) {
            if (is == null) throw new ClassNotFoundException(name);
            ClassNode classNode = new ClassNode();
            ClassReader reader = new ClassReader(is);
            reader.accept(classNode, 0);
            return classNode;
        }
    }
}
