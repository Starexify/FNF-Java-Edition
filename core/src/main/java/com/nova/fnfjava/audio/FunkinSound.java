package com.nova.fnfjava.audio;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.nova.fnfjava.Conductor;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.Paths;
import com.nova.fnfjava.data.song.SongData;
import com.nova.fnfjava.data.song.SongRegistry;
import games.rednblack.miniaudio.MASound;
import games.rednblack.miniaudio.MiniAudio;
import games.rednblack.miniaudio.loader.MASoundLoader;

public class FunkinSound {
    public MiniAudio miniAudio;
    public MASound music;

    public static Pool<OneShotSound> soundPool = new Pool<>() {
        @Override
        protected OneShotSound newObject() {
            return new OneShotSound();
        }
    };
    public static Array<OneShotSound> activeSounds = new Array<>();

    public static final int MAX_ACTIVE_SOUNDS = 32;

    public float currentMusicPitch = 1.0f;
    public String label = "unknown";

    public FunkinSound(MiniAudio miniAudio) {
        this.miniAudio = miniAudio;
        Main.assetManager.setLoader(MASound.class, new MASoundLoader(miniAudio, Main.assetManager.getFileHandleResolver()));
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
            if (Main.assetManager.isLoaded(pathToUse, MASound.class)) music = Main.assetManager.get(pathToUse, MASound.class);
            else music = miniAudio.createSound(pathToUse);

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

    public boolean playOnce(String key, float volume, boolean important) {
        if (activeSounds.size >= MAX_ACTIVE_SOUNDS && !important) {
            Main.logger.setTag("FunkinSound").warn("Cannot play sound, too many active channels: " + activeSounds.size);
            return false;
        }

        OneShotSound pooledSound = soundPool.obtain();

        if (pooledSound.load(key, volume, miniAudio)) {
            activeSounds.add(pooledSound);
            pooledSound.play();
            return true;
        } else {
            // Failed to load, return to pool
            soundPool.free(pooledSound);
            return false;
        }
    }

    public boolean playOnce(String key, float volume) {
        return playOnce(key, volume, false);
    }

    public boolean playOnce(String key) {
        return playOnce(key, 1.0f, false);
    }

    public void stopAllOneShotSounds() {
        // Copy array to avoid concurrent modification
        Array<OneShotSound> soundsCopy = new Array<>(activeSounds);
        for (OneShotSound sound : soundsCopy) sound.stop();
        activeSounds.clear();
    }

    public float getTime() {
        return (music != null) ? music.getCursorPosition() : 0f;
    }

    public float getLength() {
        return music.getLength();
    }

    public float getPitch() {
        return currentMusicPitch;
    }

    public boolean isPlaying() {
        return (music != null && music.isPlaying());
    }

    public void pause() {
        miniAudio.stopEngine();
    }

    public void resume() {
        miniAudio.startEngine();
    }

    public void dispose() {
        stopAllOneShotSounds();

        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }

        if (miniAudio != null) miniAudio.dispose();
    }

    public static class OneShotSound implements Pool.Poolable {
        private MASound sound;
        private String key;
        private boolean active;
        private Timer.Task cleanupTask;

        public boolean load(String soundKey, float volume, MiniAudio miniAudio) {
            try {
                this.key = soundKey;
                this.sound = miniAudio.createSound(soundKey);

                if (this.sound != null) {
                    this.sound.setVolume(volume);
                    this.active = true;
                    return true;
                }
                return false;
            } catch (Exception e) {
                Main.logger.setTag("FunkinSound").error("Failed to load sound: " + soundKey, e);
                return false;
            }
        }

        public void play() {
            if (sound != null && active) {
                sound.play();
                scheduleCleanup();
            }
        }

        private void scheduleCleanup() {
            if (sound != null) {
                cleanupTask = Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        stop();
                    }
                }, sound.getLength() + 0.1f);
            }
        }

        public void stop() {
            if (cleanupTask != null) {
                cleanupTask.cancel();
                cleanupTask = null;
            }

            if (sound != null) {
                sound.stop();
                sound.dispose();
                sound = null;
            }

            if (active) {
                active = false;
                activeSounds.removeValue(this, true);
                soundPool.free(this); // Return to pool
            }
        }

        public boolean isPlaying() {
            return sound != null && sound.isPlaying() && active;
        }

        @Override
        public void reset() {
            if (sound != null) {
                sound.stop();
                sound.dispose();
                sound = null;
            }
            if (cleanupTask != null) {
                cleanupTask.cancel();
                cleanupTask = null;
            }
            key = null;
            active = false;
        }
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
