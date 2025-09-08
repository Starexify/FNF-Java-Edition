package com.nova.fnfjava.lwjgl3;

public class FunkyLoader {
    private static FunkyClassLoader classLoader;

    public static void main(String[] args) {
        System.out.println("=== FunkyLoader starting ===");

        classLoader = new FunkyClassLoader(FunkyLoader.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);

        FunkyMixinBootstrap.init();

        Lwjgl3Launcher.main(args);
    }

    public static FunkyClassLoader getClassLoader() {
        return classLoader;
    }
}
