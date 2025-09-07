package com.nova.fnfjava.modding.api;

import com.badlogic.gdx.utils.Array;

public interface ModLoader {
    void dispose();
    Array<LoadedMod> getLoadedMods();

    default void loadAllMods() {}
    default void reloadChangedMods() {}
    default void forceReloadAllMods() {}
}
