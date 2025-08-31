package com.nova.fnfjava.data.song;

public class SongMetadata {
    public String songName;
    public String artist;
    public String charter;
    public float bpm;
    public String difficulty;

    public SongMetadata() {
    }

    public SongMetadata(String songName, String artist, float bpm) {
        this.songName = songName;
        this.artist = artist;
        this.bpm = bpm;
    }
}
