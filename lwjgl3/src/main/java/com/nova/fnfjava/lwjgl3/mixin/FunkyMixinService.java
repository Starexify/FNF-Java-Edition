package com.nova.fnfjava.lwjgl3.mixin;

import com.nova.fnfjava.lwjgl3.FunkyClassLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.service.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

public class FunkyMixinService extends MixinServiceAbstract {
    public static final FunkyClassLoader classLoader = FunkyClassLoader.getInstance();

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
        public Class<?> findAgentClass(String name, boolean initialize) {
            return this.findAgentClass(name, initialize);
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
            return this.getClassNode(name, runTransformers, readerFlags);
        }
    };

    public final ITransformerProvider transformerProvider = new ITransformerProvider() {
        @Override
        public Collection<ITransformer> getTransformers() {
            return Collections.emptyList();
        }

        @Override
        public Collection<ITransformer> getDelegatedTransformers() {
            return Collections.emptyList();
        }

        @Override
        public void addTransformerExclusion(String name) {
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
        return this.transformerProvider;
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
}
