package com.nova.fnfjava.lwjgl3.mixin;

import com.nova.fnfjava.lwjgl3.FunkyLoader;
import com.nova.fnfjava.lwjgl3.util.UrlUtil;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.service.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

public class FunkyMixinService extends MixinServiceAbstract {
    private final IClassProvider classProvider;
    private final IClassBytecodeProvider bytecodeProvider;
    private final ITransformerProvider transformerProvider;
    private final IClassTracker classTracker;

    public FunkyMixinService() {
        System.out.println("=== FunkyMixinService CONSTRUCTOR called ===");

        this.classProvider = new FunkyClassProvider();
        this.bytecodeProvider = new FunkyBytecodeProvider();
        this.transformerProvider = new FunkyTransformerProvider();
        this.classTracker = new FunkyClassTracker();
    }

    @Override
    public String getName() {
        return "FunkyMixin";
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public IClassProvider getClassProvider() {
        return classProvider;
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return bytecodeProvider;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return transformerProvider;
    }

    @Override
    public IClassTracker getClassTracker() {
        return classTracker;
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
        return new ContainerHandleURI(UrlUtil.LOADER_CODE_SOURCE.toUri());
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return FunkyLoader.getClassLoader().getResourceAsStream(name);
    }
}
