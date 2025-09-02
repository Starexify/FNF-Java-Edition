package com.nova.fnfjava.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Paths;

public abstract class BaseRegistry<T extends IRegistryEntry<J>, J, P> {
    public final String registryId;
    public final String dataFilePath;
    public final ObjectMap<String, T> entries;
    private final EntryConstructor<T> constructor;

    public BaseRegistry(String registryId, String dataFilePath, EntryConstructor<T> constructor) {
        this.registryId = registryId;
        this.dataFilePath = dataFilePath;
        this.entries = new ObjectMap<>();
        this.constructor = constructor;

        Gdx.app.log(getClass().getSimpleName(), "Initialized " + registryId + " registry");
    }

    // Pre-loads all registry entries from JSON into memory for fast access
    public void loadEntries() {
        clearEntries();

        Array<String> entryIdList = DataAssets.listDataFilesInPath(dataFilePath + "/");
        Array<String> unscriptedEntryIds = new Array<>();
        for (String entryId : entryIdList) if (!entries.containsKey(entryId)) unscriptedEntryIds.add(entryId);

        Gdx.app.log(registryId, "Parsing " + unscriptedEntryIds.size + " unscripted entries...");

        for (String entryId : unscriptedEntryIds) {
            try {
                J data = parseEntryData(entryId);
                if (data != null) {
                    T entry = createEntry(entryId, data);
                    entries.put(entryId, entry);

                    Gdx.app.log(registryId, "Loaded entry data: " + entryId);
                }
            } catch (Exception e) {
                Gdx.app.error(registryId, "Failed to load entry data: " + entryId, e);
            }
        }
    }

    public Array<String> listEntryIds() {
        return entries.keys().toArray();
    }

    public JsonFile loadEntryFile(String id) {
        String entryFilePath = Paths.json(dataFilePath + "/" + id);
        String rawJson = Gdx.files.internal(entryFilePath).readString("UTF-8").trim();
        return new JsonFile(entryFilePath, rawJson);
    }

    public T fetchEntry(String id) {
        return entries.get(id);
    }

    public abstract J parseEntryData(String id);

    public void printErrors(Array<String> errors, String id) {
        Gdx.app.error(registryId, "Failed to parse data for " + id);

        for (String error : errors) Gdx.app.error(registryId, error);
    }

    public T createEntry(String id, J data, Object... params) {
        T entry = constructor.create(id, params);
        entry.loadData(data);
        return entry;
    }

    public void clearEntries() {
        for (T entry : entries.values()) if (entry != null) entry.destroy();
        entries.clear();
    }

    @FunctionalInterface
    public interface EntryConstructor<T> {
        T create(String id, Object... params);
    }
}
