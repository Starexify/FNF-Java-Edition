package com.nova.fnfjava.data.freeplay.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.data.JsonFile;
import com.nova.fnfjava.ui.freeplay.charselect.PlayableCharacter;

public class PlayerRegistry extends BaseRegistry<PlayableCharacter, PlayerData, PlayerRegistry.PlayerEntryParams> {
    public static PlayerRegistry instance;

    public final Json parser = new Json();
    public ObjectMap<String, String> ownedCharacterIds = new ObjectMap<>();

    public PlayerRegistry() {
        super("PLAYER", "players", PlayableCharacter::new);

        setupParser();
    }

    public static void initialize() {
        if (instance == null) instance = new PlayerRegistry();
    }

    public void setupParser() {
        parser.setIgnoreUnknownFields(true);
    }

    @Override
    public void loadEntries() {
        super.loadEntries();

        for (String playerId : listEntryIds()) {
            PlayableCharacter player = fetchEntry(playerId);
            if (player == null) continue;

            Array<String> currentPlayerCharIds = player.getOwnedCharacterIds();
            for (String characterId : currentPlayerCharIds) ownedCharacterIds.put(characterId, playerId);
        }
    }

    @Override
    public PlayerData parseEntryData(String id) {
        try {
            JsonFile entryFile = loadEntryFile(id);
            PlayerData playerData = parser.fromJson(PlayerData.class, entryFile.contents());
            return playerData;
        } catch (Exception e) {
            Gdx.app.error("PlayerRegistry", "Failed to parse player data for: " + id, e);
            return null;
        }
    }

    public boolean isCharacterOwned(String characterId) {
        return ownedCharacterIds.containsKey(characterId);
    }

    @Override
    public PlayerEntryParams getDefaultParams(String id, PlayerData data) {
        return new PlayerEntryParams();
    }

    public record PlayerEntryParams() {}
}
