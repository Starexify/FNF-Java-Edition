package com.nova.fnfjava;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.nova.fnfjava.input.Controls;
import com.nova.fnfjava.input.KeyboardScheme;

public class PlayerSettings {
    public static int numPlayers = 0;
    public static int numAvatars = 0;

    public static PlayerSettings player1;
    public static PlayerSettings player2;

    public static final Signal<PlayerSettings> onAvatarAdd = new Signal<>();
    public static final Signal<PlayerSettings> onAvatarRemove = new Signal<>();

    public int id;

    public Controls controls;

    public static void init() {
        if (player1 == null) {
            player1 = new PlayerSettings(1);
            ++numPlayers;
        }
    }

    public PlayerSettings(int id){
        Gdx.app.log("PlayerSettings", "loading player settings for id: " + id);

        this.id = id;
        this.controls = new Controls("player" + id, KeyboardScheme.NONE);

        //addKeyboard();
    }
}
