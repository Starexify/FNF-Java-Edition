package com.nova.fnfjava.lwjgl3;

import com.nova.fnfjava.lwjgl3.mixin.FunkyMixinService;
import com.nova.fnfjava.lwjgl3.mixin.FunkyTransformer;
import com.nova.fnfjava.lwjgl3.utils.Utils;
import com.nova.fnfjava.modding.loader.FunkyModLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FunkyClassLoader extends FunkyModLoader.ExposedClassLoader {
    public static FunkyClassLoader instance;

    public ClassLoader asmClassLoader = URLClassLoader.newInstance(new URL[0], this);

    private static final String[] BLACKLISTED_PREFIXES = {
        "java.", "javax.", "sun.", "com.sun.", "jdk.", "org.spongepowered.asm."
    };

    private static final String[] PROTECTED_PREFIXES = {
        "com.nova.fnfjava.lwjgl3.mixin."
    };

    private final Map<String, URI> classCodeSourceURIs = new ConcurrentHashMap<>();

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

    // The process of class loading from their bytes and then transforming them
    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
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

    public Class<?> define(String name, boolean resolve) throws IOException, ClassNotFoundException {
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
            else {
                String path = sourceUrl.getPath();
                int seperatorIndex = path.lastIndexOf('!');
                if (seperatorIndex != -1) sourceUrl = new URL(path.substring(0, seperatorIndex));
                defined = defineClass(name, bytes, 0, bytes.length, new CodeSource(sourceUrl, (CodeSigner[]) null));
            }

            if (resolve) resolveClass(defined);

            return defined;
        } catch (LinkageError e) {
            throw new ClassNotFoundException("Invalid bytecode for class " + name, e);
        } catch (ClassNotFoundException e) {

            throw e;
        }
    }

    public RawClassData loadClassBytes(String name, boolean transform) throws IOException {
        String path = name.replace(".", "/") + ".class";
        URL url = this.findResource(path);

        InputStream input;
        if (url == null) {
            // Try getting resource from parent classloader
            input = this.getParent().getResourceAsStream(path);

            // Try system classloader as fallback
            if (input == null) input = ClassLoader.getSystemResourceAsStream(path);
        } else input = url.openStream();

        byte[] originalBytes = input.readAllBytes();
        input.close();
        byte[] transformedBytes;

        var qualifiedName = Utils.toCodeSourceURI(url, name);
        if (transform) transformedBytes = this.transformBytes(originalBytes, name, qualifiedName);
        else transformedBytes = originalBytes;

        if (originalBytes.length != transformedBytes.length) FunkyMixinService.logger.info("Transformed class: " + name + " - Original byte size: " + originalBytes.length + " Transformed byte size: " + transformedBytes.length);

        return new RawClassData(url, transformedBytes);
    }

    synchronized byte[] transformBytes(byte[] classBytecode, String qualifiedName, URI codeSourceURI) {
        if (!this.isClassProtected(qualifiedName)) {
            ClassReader reader = new ClassReader(classBytecode);
            ClassNode node = new ClassNode();

            reader.accept(node, 0);

            if (codeSourceURI != null) this.classCodeSourceURIs.putIfAbsent(node.name, codeSourceURI);

            try {
                FunkyTransformer transformer = getTransformer();
                String internalName = node.name;
                if (internalName == null) throw new NullPointerException();

                transformer.transformClass(node, codeSourceURI);
                //System.out.println("[FunkinClassLoader] " + internalName + " was transformed by a " + transformer.getClass().getSimpleName());

            } catch (Throwable t) {
                throw new RuntimeException("Error within ASM transforming process for class " + qualifiedName, t);
            }

            try {
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                    @Override
                    protected ClassLoader getClassLoader() {
                        return FunkyClassLoader.this.asmClassLoader;
                    }
                };
                node.accept(writer);
                classBytecode = Objects.requireNonNull(writer.toByteArray());
            } catch (Throwable t) {
                try {
                    StringWriter disassembledClass = new StringWriter();
                    TraceClassVisitor traceVisitor = new TraceClassVisitor(new PrintWriter(disassembledClass));
                    CheckClassAdapter checkAdapter = new CheckClassAdapter(Opcodes.ASM9, traceVisitor, true) {
                        @Override
                        public void visitInnerClass(String name, String outerName, String innerName, int access) {
                            super.visitInnerClass(name, outerName, innerName, access & ~Opcodes.ACC_SUPER);
                        }
                    };
                    node.accept(checkAdapter);

                    throw new RuntimeException("The class seems to be intact, but ASM does not like it anyways. In order to help on your debugging journey, take this:\n" + disassembledClass);
                } catch (Throwable e) {
                    t.addSuppressed(e);
                }

                throw new RuntimeException("Unable to write ASM Classnode to bytecode for class " + qualifiedName, t);
            }
        }
        return classBytecode;
    }

    public boolean isClassBlacklisted(String name) {
        for (String prefix : BLACKLISTED_PREFIXES) if (name.startsWith(prefix)) return true;
        return false;
    }

    public boolean isClassProtected(String name) {
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
    }
}
