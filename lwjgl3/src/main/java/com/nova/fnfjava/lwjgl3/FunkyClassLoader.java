package com.nova.fnfjava.lwjgl3;

import java.net.URL;
import java.net.URLClassLoader;

public class FunkyClassLoader extends URLClassLoader {
    public static FunkyClassLoader instance;

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

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
