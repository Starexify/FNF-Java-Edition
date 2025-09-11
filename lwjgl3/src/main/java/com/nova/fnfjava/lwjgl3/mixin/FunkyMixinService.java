package com.nova.fnfjava.lwjgl3.mixin;

import com.nova.fnfjava.lwjgl3.FunkyClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.IConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class FunkyMixinService extends MixinServiceAbstract {
    public static FunkyMixinService instance;

    public static final FunkyClassLoader classLoader = FunkyClassLoader.getInstance();

    @Override
    public void init() {
        FunkyMixinService.instance = this;
        super.init();
    }

    @Override
    public String getName() {
        return "FunkyMixin";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public final IClassProvider classProvider = new IClassProvider() {
        @Override
        public URL[] getClassPath() {
            return classLoader.getURLs();
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                return FunkyMixinService.classLoader.findClass(name);
            } catch (ClassNotFoundException e) {
                System.out.println("[FunkyMixinService] Unable to find class" + e.getMessage());
                throw e;
            }
        }

        @Override
        public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
            return Class.forName(name, initialize, classLoader);
        }

        @Override
        public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
            return Class.forName(name, initialize, ClassLoader.getSystemClassLoader());
        }
    };

    public final IClassBytecodeProvider bytecodeProvider = new IClassBytecodeProvider() {
        @Override
        public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
            return this.getClassNode(name, false);
        }

        @Override
        public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
            return this.getClassNode(name, runTransformers, 0);
        }

        @Override
        public ClassNode getClassNode(String name, boolean runTransformers, int readerFlags) throws ClassNotFoundException, IOException {
            List<Exception> caughtExceptions = new ArrayList<>();

            Callable<ClassReader>[] suppliers = new Callable[4];

            int i = 0;
            int systemClassLoaderIndex;

            if (FunkyMixinService.classLoader.isClassProtected(name)) systemClassLoaderIndex = i++;
            else systemClassLoaderIndex = suppliers.length - 1;


            suppliers[systemClassLoaderIndex] = () -> {
                ClassLoader cl = ClassLoader.getPlatformClassLoader();
                InputStream input;
                if (cl == null) input = ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class");
                else input = cl.getResourceAsStream(name.replace('.', '/') + ".class");

                return new ClassReader(input);
            };


            suppliers[i++] = () -> {
                return new ClassReader(FunkyMixinService.classLoader.loadClassBytes(name, false).getBytes());
            };

            /*suppliers[i++] = () -> {
                return new ClassReader(FunkyMixinService.classLoader.loadBytesWithChildren(name, false));
            };*/

            suppliers[i++] = () -> {
                return new ClassReader(FunkyMixinService.classLoader.getResourceAsStream(name.replace('.', '/') + ".class"));
            };

            for (Callable<ClassReader> supplier : suppliers) {
                try {
                    @SuppressWarnings("null")
                    ClassReader reader = supplier.call();
                    ClassNode node = new ClassNode();
                    reader.accept(node, readerFlags);
                    return node;
                } catch (Exception e) {
                    caughtExceptions.add(e);
                }
            }

            Exception causedBy;
            ListIterator<Exception> it = caughtExceptions.listIterator(caughtExceptions.size());

            if (it.hasPrevious()) {
                causedBy = it.previous();
                while (it.hasPrevious()) {
                    causedBy.addSuppressed(it.previous());
                }
            } else {
                causedBy = null;
            }

            ClassNotFoundException thrownException = new ClassNotFoundException("Could not load ClassNode with name " + name, causedBy);

            throw thrownException;
        }
    };

    @Override
    public IClassProvider getClassProvider() {
        return this.classProvider;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return this.bytecodeProvider;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return null;
    }

    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return Collections.singletonList("org.spongepowered.asm.launch.platform.MixinPlatformAgentDefault");
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        return new ContainerHandleVirtual(this.getName());
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return FunkyClassLoader.getInstance().getResourceAsStream(name);
    }

    @Override
    protected ILogger createLogger(String name) {
        return new FunkyMixinLogger(name);
    }

    public final <T extends IMixinInternal> T getMixinInternal(Class<T> type) {
        return this.getInternal(type);
    }


    @Override
    public void unwire() {
        super.unwire();
        this.wiredPhaseConsumer = null;
    }

    @Override
    public void wire(MixinEnvironment.Phase phase, IConsumer<MixinEnvironment.Phase> phaseConsumer) {
        super.wire(phase, phaseConsumer);
        this.wiredPhaseConsumer = phaseConsumer;
    }

    private IConsumer<MixinEnvironment.Phase> wiredPhaseConsumer;

    public IConsumer<MixinEnvironment.Phase> getPhaseConsumer() {
        return this.wiredPhaseConsumer;
    }
}
