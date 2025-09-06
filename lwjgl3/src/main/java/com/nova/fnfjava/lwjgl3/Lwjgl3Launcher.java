package com.nova.fnfjava.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.util.Constants;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.InputStream;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {

    static {
        initializeMixins();
    }

    private static void initializeMixins() {
        try {
            System.out.println("=== MIXIN INITIALIZATION START ===");

            // Set system properties
            System.setProperty("mixin.debug", "true");
            System.setProperty("mixin.debug.verbose", "true");
            System.setProperty("mixin.debug.export", "true");
            System.setProperty("mixin.checks", "true");
            System.setProperty("mixin.hotSwap", "false");
            System.setProperty("mixin.service", "com.nova.fnfjava.lwjgl3.mixins.StandaloneMixinService");

            // Bootstrap the Mixin framework
            MixinBootstrap.init();

            // Set up environment
            MixinEnvironment environment = MixinEnvironment.getDefaultEnvironment();
            environment.setSide(MixinEnvironment.Side.CLIENT);

            System.out.println("=== ADDING MIXIN CONFIGURATION ===");

            // Check if the configuration file exists
            InputStream configStream = Lwjgl3Launcher.class.getClassLoader()
                .getResourceAsStream("mixins.fnfje.json");
            if (configStream == null) {
                System.err.println("ERROR: mixins.fnfje.json not found in classpath!");
                // Try to list available resources
                System.err.println("Available resources:");
                try {
                    java.util.Enumeration<java.net.URL> resources =
                        Lwjgl3Launcher.class.getClassLoader().getResources("");
                    while (resources.hasMoreElements()) {
                        System.err.println("- " + resources.nextElement());
                    }
                } catch (Exception e) {
                    System.err.println("Could not list resources: " + e.getMessage());
                }
            } else {
                configStream.close();
                System.out.println("Found mixins.fnfje.json in classpath");
            }

            // Add configuration - this MUST happen before any target classes are loaded
            try {
                Mixins.addConfiguration("mixins.fnfje.json");
                System.out.println("Successfully added mixin configuration");
            } catch (Exception e) {
                System.err.println("Failed to add mixin configuration: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }

            System.out.println("=== MIXIN INITIALIZATION SUCCESS ===");
            System.out.println("Mixin Environment Phase: " + environment.getPhase());
            System.out.println("Mixin Environment Side: " + environment.getSide());

        } catch (Exception e) {
            System.err.println("=== MIXIN INITIALIZATION FAILED ===");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Mixins", e);
        }
    }


    public static void main(String[] args) {
        // Initialize mixins FIRST - before ANY other code
        // NO imports of TestClass anywhere in this file!

        System.out.println("=== TESTING MIXIN ===");

        try {
            // Force the class to be loaded through the ClassLoader that has mixin support
            ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
            System.out.println("Current ClassLoader: " + currentLoader);

            // Load TestClass for the FIRST TIME here via the mixin-aware class loader
            Class<?> testClass = currentLoader.loadClass("com.nova.fnfjava.TestClass");
            System.out.println("Loaded TestClass: " + testClass);
            System.out.println("TestClass ClassLoader: " + testClass.getClassLoader());

            // Create instance and call methods
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            // Call methods via reflection
            testClass.getMethod("testMethod").invoke(testInstance);
            testClass.getMethod("staticTestMethod").invoke(null);

        } catch (Exception e) {
            System.err.println("Failed to test mixins:");
            e.printStackTrace();
        }

        System.out.println("=== STARTING APPLICATION ===");
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static void testMixins() {
        System.out.println("=== TESTING MIXIN ===");

        try {
            // Load TestClass via reflection to ensure it wasn't loaded earlier
            Class<?> testClass = Class.forName("com.nova.fnfjava.TestClass");
            Object testInstance = testClass.getDeclaredConstructor().newInstance();

            // Call methods
            testClass.getMethod("testMethod").invoke(testInstance);
            testClass.getMethod("staticTestMethod").invoke(null);

        } catch (Exception e) {
            System.err.println("Failed to test mixins:");
            e.printStackTrace();
        }
    }

    private static Lwjgl3Application createApplication() {
        Main main = new Main();
        Lwjgl3Application app = new Lwjgl3Application(main, getDefaultConfiguration());
        return app;
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle(Constants.TITLE);
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(false);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        //configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setWindowedMode(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        configuration.setPauseWhenLostFocus(true);

        configuration.setPreferencesConfig("FunkinJE", Files.FileType.Internal);

        configuration.setIdleFPS(30);

        return configuration;
    }
}
