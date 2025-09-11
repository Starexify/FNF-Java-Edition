package com.nova.fnfjava.lwjgl3;

import com.nova.fnfjava.lwjgl3.mixin.FunkyTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Objects;

public class FunkyClassLoader extends URLClassLoader {
    public static FunkyClassLoader instance;

    public ClassLoader asmClassLoader = URLClassLoader.newInstance(new URL[0], this);

    private static final String[] BLACKLISTED_PREFIXES = {
        "java.", "javax.", "sun.", "com.sun.", "jdk.", "org.spongepowered.asm."
    };

    private static final String[] PROTECTED_PREFIXES = {

    };

    public FunkyClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public static FunkyClassLoader getInstance() {
        if (FunkyClassLoader.instance == null) {
            synchronized (FunkyClassLoader.class) {
                if (FunkyClassLoader.instance == null) FunkyClassLoader.instance = new FunkyClassLoader(FunkyClassLoader.class.getClassLoader());
            }
        }
        return FunkyClassLoader.instance;
    }

    // overridden for public visibility
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return this.define(name, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (isClassBlacklisted(name)) return super.loadClass(name, resolve);

        try {
            return define(name, resolve);
        } catch (ClassNotFoundException cnfe1) {
            try {
                return super.loadClass(name, resolve);
            } catch (ClassNotFoundException cnfe2) {
                cnfe2.addSuppressed(cnfe1);
                throw cnfe2;
            }
        } catch (IOException e) {
            throw new RuntimeException("IO error while loading class " + name, e);
        }
    }

    private Class<?> define(String name, boolean resolve) throws IOException, ClassNotFoundException {
        try {
            RawClassData rawClass;
            try {
                boolean transform = !isClassProtected(name);
                rawClass = loadClassBytes(name, transform);
            } catch (Throwable t) {
                throw new ClassNotFoundException("Unable to load bytes for " + name, t);
            }

            Class<?> defined;
            byte[] bytes = rawClass.getBytes();
            URL sourceUrl = rawClass.getSource();

            if (sourceUrl == null) defined = defineClass(name, bytes, 0, bytes.length);
            else defined = defineClass(name, bytes, 0, bytes.length, new CodeSource(sourceUrl, (CodeSigner []) null));

            System.out.println("[DEBUG] Loaded class: " + defined);
            if (resolve) resolveClass(defined);

            return defined;
        } catch (LinkageError e) {
            throw new ClassNotFoundException("Invalid bytecode for class " + name, e);
        }
    }

    private RawClassData loadClassBytes(String name, boolean transform) throws IOException {
        String path = name.replace(".", "/") + ".class";
        URL url = this.findResource(path);

        InputStream input;
        if (url == null) {
            // Try getting resource from parent classloader
            input = this.getParent().getResourceAsStream(path);

            // Try system classloader as fallback
            if (input == null) input = ClassLoader.getSystemResourceAsStream(path);
        } else input = url.openStream();

        try {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(input);
            reader.accept(node, 0);

            // Apply mixin transformations
            if (transform) getTransformer().transformClass(name, node);

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                @Override
                protected ClassLoader getClassLoader() {
                    return FunkyClassLoader.this.asmClassLoader;
                }
            };
            node.accept(writer);
            byte[] transformedCode = writer.toByteArray();

            URL sourceUrl = url;
            if (sourceUrl != null) {
                String urlPath = sourceUrl.getPath();
                int separatorIndex = urlPath.lastIndexOf('!');
                if (separatorIndex != -1) {
                    sourceUrl = new URL(urlPath.substring(0, separatorIndex));
                }
            }

            return new RawClassData(sourceUrl, transformedCode);
        } finally {
            input.close();
        }
    }

    private boolean isClassBlacklisted(String name) {
        for (String prefix : BLACKLISTED_PREFIXES) if (name.startsWith(prefix)) return true;
        return false;
    }
    private boolean isClassProtected(String name) {
        for (String prefix : PROTECTED_PREFIXES) if (name.startsWith(prefix)) return true;
        return false;
    }

    public FunkyTransformer getTransformer() {
        return Lwjgl3Launcher.transformer;
    }

    public static class RawClassData {
        private final byte[] bytes;
        private final URL source;

        public RawClassData(URL source, byte[] bytes) {
            this.source = source;
            this.bytes = Objects.requireNonNull(bytes, "bytes must not be null");
        }

        public byte[] getBytes() {
            return this.bytes;
        }

        public URL getSource() {
            return this.source;
        }

        @Override
        public String toString() {
            return "RawClassData{bytes=" + bytes + ", source=" + source + '}';
        }
    }
}
