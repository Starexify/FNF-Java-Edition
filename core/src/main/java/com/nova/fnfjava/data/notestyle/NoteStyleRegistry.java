package com.nova.fnfjava.data.notestyle;

import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.play.notes.notestyle.NoteStyle;
import com.nova.fnfjava.util.Constants;

public class NoteStyleRegistry extends BaseRegistry<NoteStyle, NoteStyleData, NoteStyleRegistry.NoteStyleEntryParams> {
    public static final NoteStyleRegistry instance = new NoteStyleRegistry();

    public NoteStyleRegistry() {
        super("NOTESTYLE", "notestyles", NoteStyle::new);
    }

    public NoteStyle fetchDefault() {
        NoteStyle notestyle = fetchEntry(Constants.DEFAULT_NOTE_STYLE);
        if (notestyle == null) throw new IllegalStateException("Default notestyle was null! This should not happen!");
        return notestyle;
    }

    @Override
    public NoteStyleData parseEntryData(String id) {
        return parseJsonData(id, NoteStyleData.class);
    }

    @Override
    public NoteStyleEntryParams getDefaultParams(String id, NoteStyleData data) {
        return new NoteStyleEntryParams();
    }

    public record NoteStyleEntryParams() {}
}
