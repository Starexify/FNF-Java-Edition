package com.nova.fnfjava.lwjgl3;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.lwjgl3.mixin.FunkyMixinService;
import com.nova.fnfjava.lwjgl3.mixin.FunkyTransformer;
import com.nova.fnfjava.lwjgl3.modding.FunkyModLoader;
import com.nova.fnfjava.util.Constants;
import org.spongepowered.asm.mixin.MixinEnvironment;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static FunkyTransformer transformer;

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;

        FunkyMixinBootstrap.init();

        transformer = new FunkyTransformer(FunkyMixinService.instance);
        FunkyClassLoader cl = FunkyClassLoader.getInstance();

        cl.setTransformer(transformer);
        cl.setMixinService(FunkyMixinService.instance);

        MixinExtrasBootstrap.init();
        FunkyMixinService.instance.getPhaseConsumer().accept(MixinEnvironment.Phase.PREINIT);

        FunkyMixinService.instance.getPhaseConsumer().accept(MixinEnvironment.Phase.INIT);
        FunkyMixinService.instance.getPhaseConsumer().accept(MixinEnvironment.Phase.DEFAULT);

        // Create application using Reflection and a custom ClassLoader that handles ASM/Mixin Transformation
        try {
            Class<?> lwjglApp = cl.loadClass("com.nova.fnfjava.lwjgl3.Lwjgl3Launcher$ReflectionBootstrap");
            lwjglApp.getDeclaredMethod("createApplication").invoke(null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static class ReflectionBootstrap {
        public static Lwjgl3Application createApplication() {
            Main main = new Main();
            main.setModLoader(new FunkyModLoader(FunkyClassLoader.getInstance()));
            return new Lwjgl3Application(main, getDefaultConfiguration());
        }

        private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
            Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
            configuration.setTitle(Constants.TITLE);
            configuration.useVsync(false);
            //configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
            configuration.setWindowedMode(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
            configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
            configuration.setPauseWhenLostFocus(true);
            configuration.setPreferencesConfig("FunkinJE", Files.FileType.Internal);
            configuration.setIdleFPS(30);
            return configuration;
        }
    }
}
