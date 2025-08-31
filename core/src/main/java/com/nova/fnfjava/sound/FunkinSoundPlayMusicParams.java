package com.nova.fnfjava.sound;

public class FunkinSoundPlayMusicParams {
    public boolean mapTimeChanges = true;

    public boolean isMapTimeChanges() {
        return mapTimeChanges;
    }

    public static class Builder {
        private final FunkinSoundPlayMusicParams params = new FunkinSoundPlayMusicParams();

        public Builder mapTimeChanges(boolean b) { params.mapTimeChanges = b; return this; }

        public FunkinSoundPlayMusicParams build() { return params; }
    }
}
