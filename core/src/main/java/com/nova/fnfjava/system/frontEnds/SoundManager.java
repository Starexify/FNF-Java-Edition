package com.nova.fnfjava.system.frontEnds;

import com.badlogic.gdx.audio.Music;

public class SoundManager {
    public Music music;

    public void playMusic(Music newMusic) {
        if (music != null) music.stop();
        music = newMusic;
        music.play();
    }

    public float getMusicTime() {
        return (music != null) ? music.getPosition() : 0f;
    }

    public boolean isMusicPlaying() {
        return (music != null && music.isPlaying());
    }
}
