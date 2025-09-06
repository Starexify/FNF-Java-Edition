package com.nova.fnfjava.data.freeplay.player;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.ui.freeplay.charselect.PlayableCharacter;

public class PlayerRegistry extends BaseRegistry<PlayableCharacter, PlayerData, PlayerRegistry.PlayerEntryParams> {
    public static final PlayerRegistry instance = new PlayerRegistry();

    public ObjectMap<String, String> ownedCharacterIds = new ObjectMap<>();

    public PlayerRegistry() {
        super("PLAYER", "players", PlayableCharacter::new);
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
        return parseJsonData(id, PlayerData.class);
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
