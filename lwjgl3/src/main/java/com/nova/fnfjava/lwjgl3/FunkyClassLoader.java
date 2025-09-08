package com.nova.fnfjava.lwjgl3;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FunkyClassLoader extends URLClassLoader {
    private final ConcurrentHashMap<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();
    private final List<IClassTransformer> transformers = new ArrayList<>();

    public FunkyClassLoader(ClassLoader parent) {
        super(getClassPathUrls(), parent);
    }

    private static URL[] getClassPathUrls() {
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(File.pathSeparator);
        URL[] urls = new URL[paths.length];

        for (int i = 0; i < paths.length; i++) {
            try {
                File file = new File(paths[i]);
                urls[i] = file.toURI().toURL();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create URL for classpath entry: " + paths[i], e);
            }
        }
        return urls;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> loadedClass = loadedClasses.get(name);
        if (loadedClass != null) {
            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }

        if (name.startsWith("java.") || name.startsWith("javax.") ||
            name.startsWith("sun.") || name.startsWith("org.spongepowered.asm.")) {
            return super.loadClass(name, resolve);
        }

        try {
            byte[] classBytes = getClassBytes(name);
            if (classBytes != null) {
                // Apply transformations
                for (IClassTransformer transformer : transformers) {
                    try {
                        classBytes = transformer.transform(name, name, classBytes);
                    } catch (Exception e) {
                        System.err.println("Transformer failed for class " + name + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                Class<?> clazz = defineClass(name, classBytes, 0, classBytes.length);
                loadedClasses.put(name, clazz);

                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }
        } catch (Exception e) {
            // Fall back to parent loader
        }

        return super.loadClass(name, resolve);
    }

    public byte[] getClassBytes(String name) throws IOException {
        String resourceName = name.replace('.', '/') + ".class";

        try (InputStream is = getResourceAsStream(resourceName)) {
            if (is == null) {
                return null;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        }
    }

    public void addTransformer(IClassTransformer transformer) {
        transformers.add(transformer);
    }

    public List<IClassTransformer> getTransformers() {
        return new ArrayList<>(transformers);
    }
}
