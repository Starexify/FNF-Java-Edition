package com.nova.fnfjava.sound;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.Map;

public class FunkinSound {
    public Music music;

    private static Map<String, Sound> soundCache = new HashMap<>();

    public void playMusic(Music newMusic) {
        if (music != null) music.stop();
        music = newMusic;
        music.play();
    }

    public static void playOnce(String key, float volume) {
        Sound soundEffect = soundCache.computeIfAbsent(key, k -> Gdx.audio.newSound(Gdx.files.internal(k)));
        soundEffect.play(volume);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                soundEffect.dispose();
            }
        } , 5f);
    }

    public static void playOnce(String key) {
        playOnce(key, 1.0f);
    }

    public float getMusicTime() {
        return (music != null) ? music.getPosition() : 0f;
    }

    public boolean isMusicPlaying() {
        return (music != null && music.isPlaying());
    }

    public static void dispose() {
        for (Sound sound : soundCache.values()) sound.dispose();
        soundCache.clear();
    }
}
