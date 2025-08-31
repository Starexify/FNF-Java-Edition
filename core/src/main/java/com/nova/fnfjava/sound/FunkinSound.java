package com.nova.fnfjava.sound;

import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.data.song.SongMusicData;
import com.nova.fnfjava.data.song.SongRegistry;
import games.rednblack.miniaudio.MASound;
import games.rednblack.miniaudio.MiniAudio;

import java.util.HashMap;
import java.util.Map;

public class FunkinSound {
    public MiniAudio miniAudio;
    public MASound music;
    public static Map<String, MASound> soundCache = new HashMap<>();

    public boolean paused;

    public FunkinSound(MiniAudio miniAudio) {
        this.miniAudio = miniAudio;
    }

    public void playMusic(String key, FunkinSoundPlayMusicParams params) {
        if (music != null) music.stop();
        if (params == null || params.isMapTimeChanges()) {
            SongMusicData songMusicData = SongRegistry.instance.parseMusicData(key);
            if (songMusicData != null) {
                Conductor.getInstance().mapTimeChanges(songMusicData.timeChanges);
            }
        }
        music = miniAudio.createSound("music/" + key + "/" + key + ".ogg");
        music.play();
    }

    public void playOnce(String key, float volume) {
        MASound soundEffect = soundCache.computeIfAbsent(key, k -> miniAudio.createSound(k));
        soundEffect.setVolume(volume);
        soundEffect.play();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                soundEffect.stop();
                soundEffect.dispose();
            }
        }, soundEffect.getLength());
    }

    public void playOnce(String key) {
        playOnce(key, 1.0f);
    }

    public float getMusicTime() {
        return (music != null) ? music.getCursorPosition() : 0f;
    }

    public float getMusicLength() {
        return music.getLength();
    }

    public float getMusicPitch() {
        return 1;
    }

    public boolean isMusicPlaying() {
        return (music != null && music.isPlaying());
    }

    public void pause() {
        miniAudio.stopEngine();
    }

    public void resume() {
        miniAudio.startEngine();
    }

    public void dispose() {
        for (MASound sound : soundCache.values()) sound.dispose();
        soundCache.clear();
        music.dispose();
        miniAudio.dispose();
    }
}
