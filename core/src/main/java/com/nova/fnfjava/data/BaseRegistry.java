package com.nova.fnfjava.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class BaseRegistry<T extends IRegistryEntry<D>, D, P> {
    public final String registryId;
    public final String dataFilePath;
    public final ObjectMap<String, T> entries;

    public BaseRegistry(String registryId, String dataFilePath) {
        this.registryId = registryId;
        this.dataFilePath = dataFilePath;
        this.entries = new ObjectMap<>();

        Gdx.app.log(getClass().getSimpleName(), "Initialized " + registryId + " registry");
    }
}

@FunctionalInterface
interface EntryConstructor<T extends IRegistryEntry<?>> {
    T create(String id);
}
