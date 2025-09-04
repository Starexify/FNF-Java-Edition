package com.nova.fnfjava.play;

import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.song.SongData;
import com.nova.fnfjava.util.Constants;

public class Song implements IRegistryEntry<SongData.SongMetadata> {
    public static final String DEFAULT_SONGNAME = "Unknown";

    public String id;
    public SongData.SongMetadata metadata;

    public final ObjectMap<String, SongData.SongMetadata> _metadata;

    public String songName;

    public Song(String id, Object... params) {
        this.id = id;

        _metadata = getData() == null ? new ObjectMap<>() : new ObjectMap<String, SongData.SongMetadata>() {{put(Constants.DEFAULT_VARIATION, getData());}};
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SongData.SongMetadata getData() {
        return metadata;
    }

    @Override
    public void loadData(SongData.SongMetadata data) {
        if (data == null) throw new IllegalArgumentException("SongMetadata cannot be null");
        this.metadata = data;
    }

    public String getSongName() {
        if (getData() != null) return getData().songName != null ? getData().songName : DEFAULT_SONGNAME;
        if (_metadata.size > 0) return _metadata.get(Constants.DEFAULT_VARIATION).songName != null ? _metadata.get(Constants.DEFAULT_VARIATION).songName : DEFAULT_SONGNAME;
        return DEFAULT_SONGNAME;
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
