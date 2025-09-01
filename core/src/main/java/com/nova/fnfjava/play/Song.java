package com.nova.fnfjava.play;

import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.song.SongMetadata;

public class Song implements IRegistryEntry<SongMetadata> {
    public String id;
    public SongMetadata metadata;

    public Song(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SongMetadata getData() {
        return metadata;
    }

    @Override
    public void destroy() {

    }

    @Override
    public String toString() {
        return "Song{" +
            "id='" + id + '\'' +
            ", metadata=" + metadata +
            '}';
    }
}
