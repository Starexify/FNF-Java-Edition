package com.nova.fnfjava.play.notes.notestyle;

import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.notestyle.NoteStyleData;
import com.nova.fnfjava.data.notestyle.NoteStyleRegistry;

public class NoteStyle implements IRegistryEntry<NoteStyleData> {
    public String id;
    public NoteStyleData noteStyleData;

    public NoteStyle(String id, NoteStyleRegistry.NoteStyleEntryParams params) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public NoteStyleData getData() {
        return this.noteStyleData;
    }

    @Override
    public void loadData(NoteStyleData data) {
        if (data == null) throw new IllegalArgumentException("StageData cannot be null");
        this.noteStyleData = data;
    }

    @Override
    public void destroy() {
    }
}
