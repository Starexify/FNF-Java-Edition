package com.nova.fnfjava.modding.api;

import com.badlogic.gdx.utils.Array;

public interface ModLoader {
    default void loadAllModsWithProgress(ModProgressCallback callback) {}
    Array<LoadedMod> getLoadedMods();
    default void updateMods() {}
    default void renderMods() {}
    default void pauseMods() {}
    default void resumeMods() {}

    default void loadAllMods() {}
    default void reloadChangedMods() {}
    default void forceReloadAllMods() {}
    default void dispose() {}

    interface ModProgressCallback {
        void onProgress(String modName, float progress, String status);
    }
}
