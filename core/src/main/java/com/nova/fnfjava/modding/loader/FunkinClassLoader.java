package com.nova.fnfjava.modding.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class FunkinClassLoader extends URLClassLoader {
    private final String modId;

    public FunkinClassLoader(URL[] urls, ClassLoader parent, String modId) {
        super(urls, parent);
        this.modId = modId;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Class not found in mod '" + modId + "': " + name, e);
        }
    }
}
