package com.nova.fnfjava.play.notes.notekind;

public class NoteKind {
    public String noteKind;
    public String description;
    public String noteStyleId;
    //public Array<NoteKindParam> params;
    public boolean scoreable = true;

    public NoteKind(String noteKind, String description, String noteStyleId) {
        this.noteKind = noteKind;
        this.description = description;
        this.noteStyleId = noteStyleId;
    }
}
