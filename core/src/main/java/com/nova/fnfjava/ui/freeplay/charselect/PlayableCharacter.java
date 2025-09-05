package com.nova.fnfjava.ui.freeplay.charselect;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.freeplay.player.PlayerData;
import com.nova.fnfjava.data.freeplay.player.PlayerRegistry;

public class PlayableCharacter implements IRegistryEntry<PlayerData> {
    public String id;
    public PlayerData playerData;

    public PlayableCharacter(String id, PlayerRegistry.PlayerEntryParams params) {
        this.id = id;
    }

    public Array<String> getOwnedCharacterIds() {
        return getData().ownedChars != null ? getData().ownedChars : new Array<>();
    }

    public boolean shouldShowUnownedChars() {
        return getData().showUnownedChars != null ? getData().showUnownedChars : false;
    }

    public boolean shouldShowCharacter(String id) {
        if (getOwnedCharacterIds().contains(id, false)) return true;

        if (shouldShowUnownedChars()) {
            boolean result = !PlayerRegistry.instance.isCharacterOwned(id);
            return result;
        }

        return false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public PlayerData getData() {
        return playerData;
    }

    @Override
    public void loadData(PlayerData data) {
        if (data == null) throw new IllegalArgumentException("PlayerData cannot be null");
        this.playerData = data;
    }

    @Override
    public void destroy() {
    }
}
