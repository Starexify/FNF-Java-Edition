package com.nova.fnfjava.data.song;

enum SongTimeFormat {
    TICKS("ticks"),
    FLOAT("float"),
    MILLISECONDS("ms");

    public final String id;

    SongTimeFormat(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static SongTimeFormat fromString(String id) {
        for (SongTimeFormat format : values()) if (format.getId().equals(id)) return format;
        return MILLISECONDS;
    }

    @Override
    public String toString() {
        return id;
    }
}

