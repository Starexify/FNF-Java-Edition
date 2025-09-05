package com.nova.fnfjava.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;

public abstract class BaseRegistry<T extends IRegistryEntry<J>, J, P> {
    public final String registryId;
    public final String dataFilePath;
    public final ObjectMap<String, T> entries;
    private final EntryConstructor<T, P> constructor;

    public BaseRegistry(String registryId, String dataFilePath, EntryConstructor<T, P> constructor) {
        this.registryId = registryId;
        this.dataFilePath = dataFilePath;
        this.entries = new ObjectMap<>();
        this.constructor = constructor;

        Main.logger.setTag("Registry").info("Initialized " + registryId + " registry");
    }

    // Pre-loads all registry entries from JSON into memory for fast access
    public void loadEntries() {
        clearEntries();

        Array<String> entryIdList = DataAssets.listDataFilesInPath(dataFilePath + "/");
        Array<String> unscriptedEntryIds = new Array<>();
        for (String entryId : entryIdList) if (!entries.containsKey(entryId)) unscriptedEntryIds.add(entryId);

        Main.logger.setTag(registryId).info("Parsing " + unscriptedEntryIds.size + " unscripted entries...");

        for (String entryId : unscriptedEntryIds) {
            try {
                J data = parseEntryData(entryId);
                if (data != null) {
                    P defaultParams = getDefaultParams(entryId, data);
                    T entry = createEntry(entryId, data, defaultParams);
                    entries.put(entryId, entry);

                    Main.logger.setTag(registryId).info("Loaded entry data: " + entryId);
                }
            } catch (Exception e) {
                Main.logger.setTag(registryId).error("Failed to load entry data: " + entryId, e);
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

    public T fetchEntry(String id, P params) {
        return entries.get(id);
    }

    public T fetchEntry(String id) {
        return fetchEntry(id, null);
    }

    public abstract J parseEntryData(String id);

    public T createEntry(String id, J data, P params) {
        T entry = constructor.create(id, params);
        entry.loadData(data);
        return entry;
    }

    public void clearEntries() {
        for (T entry : entries.values()) if (entry != null) entry.destroy();
        entries.clear();
    }

    public abstract P getDefaultParams(String id, J data);

    @FunctionalInterface
    public interface EntryConstructor<T, P> {
        T create(String id, P params);
    }
}
