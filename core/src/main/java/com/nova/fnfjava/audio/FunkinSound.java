package com.nova.fnfjava.audio;

import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.song.SongData;
import com.nova.fnfjava.data.song.SongRegistry;
import games.rednblack.miniaudio.MASound;
import games.rednblack.miniaudio.MiniAudio;

import java.util.HashMap;
import java.util.Map;

public class FunkinSound {
    public MiniAudio miniAudio;
    public MASound music;
    public static Map<String, MASound> soundCache = new HashMap<>();

    public float currentMusicPitch = 1.0f;

    public String label = "unknown";

    public FunkinSound(MiniAudio miniAudio) {
        this.miniAudio = miniAudio;
    }

    public boolean playMusic(String key, FunkinSoundPlayMusicParams params) {
        if (!params.overrideExisting && music != null && music.isPlaying()) return false;

        // Check if we're trying to play the same music that's already playing
        if (!params.restartTrack && music != null && music.isPlaying())
            if (label.equals(Paths.music(key + "/" + key))) return false;

        if (music != null) music.stop();

        if (params.mapTimeChanges) {
            SongData.SongMusicData songMusicData = SongRegistry.instance.parseMusicData(key);
            if (songMusicData != null) {
                Conductor.getInstance().mapTimeChanges(songMusicData.timeChanges);
                if (songMusicData.looped != null && !params.hasExplicitLoop) params.loop = songMusicData.looped;
            } else
                Main.logger.setTag("FunkinSound").warn("Tried and failed to find music metadata for " + key);
        }

        Paths.PathsFunction pathsFunction = params.pathsFunction != null ? params.pathsFunction : Paths.PathsFunction.MUSIC;
        String suffix = params.suffix != null ? params.suffix : "";
        String pathToUse = pathsFunction == Paths.PathsFunction.INST ? Paths.inst(key, suffix) : Paths.music(key + "/" + key);

        try {
            music = miniAudio.createSound(pathToUse);
            if (music != null) {
                label = pathToUse;
                music.setVolume(params.startingVolume);
                music.setLooping(params.loop);
                music.play();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Main.logger.setTag("FunkinSound").error("Failed to load music: " + pathToUse, e);
            return false;
        }
    }

    public void playOnce(String key, float volume) {
        MASound soundEffect = soundCache.computeIfAbsent(key, k -> miniAudio.createSound(k));
        soundEffect.setVolume(volume);
        soundEffect.play();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                soundEffect.stop();
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
        return currentMusicPitch;
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
        for (MASound sound : soundCache.values()) {
            if (sound != null) {
                sound.stop();
                sound.dispose();
            }
        }
        soundCache.clear();

        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }

        if (miniAudio != null) miniAudio.dispose();
    }

    public static class FunkinSoundPlayMusicParams {
        public float startingVolume = 1.0f;
        public String suffix = "";
        public boolean overrideExisting = false;
        public boolean restartTrack = false;
        public boolean loop = true;
        public boolean mapTimeChanges = true;
        public Paths.PathsFunction pathsFunction = Paths.PathsFunction.MUSIC;
        public boolean persist;

        public boolean hasExplicitLoop = false;

        public static class Builder {
            private final FunkinSoundPlayMusicParams params = new FunkinSoundPlayMusicParams();

            public Builder startingVolume(float v) { params.startingVolume = v; return this; }
            public Builder suffix(String s) { params.suffix = s; return this; }
            public Builder overrideExisting(boolean b) { params.overrideExisting = b; return this; }
            public Builder restartTrack(boolean b) { params.restartTrack = b; return this; }
            public Builder loop(boolean b) { params.loop = b; return this; }
            public Builder mapTimeChanges(boolean b) { params.mapTimeChanges = b; return this; }
            public Builder pathsFunction(Paths.PathsFunction b) { params.pathsFunction = b; return this; }
            public Builder persist(boolean b) { params.persist = b; return this; }

            public FunkinSoundPlayMusicParams build() { return params; }
        }
    }
}
