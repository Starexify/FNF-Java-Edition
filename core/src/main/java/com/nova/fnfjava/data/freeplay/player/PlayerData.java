package com.nova.fnfjava.data.freeplay.player;

import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.util.Constants;

public class PlayerData {
    public String name = "Unknown";
    public Array<String> ownedChars = new Array<>();
    public Boolean showUnownedChars = false;
    public String stickerPack = Constants.DEFAULT_STICKER_PACK;
    public String freeplayStyle = Constants.DEFAULT_FREEPLAY_STYLE;
    //public PlayerFreeplayDJData freeplayDJ = null;
    //public PlayerCharSelectData charSelect = null;
    //public PlayerResultsData results = null;
    public boolean unlocked = true;

    public PlayerData() {
    }
}
