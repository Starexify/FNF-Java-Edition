package com.nova.fnfjava.play.notes.notekind;

import com.badlogic.gdx.utils.ObjectMap;

public class NoteKindManager {
    public static ObjectMap<String, NoteKind> noteKinds = new ObjectMap<>();

    public static NoteKind getNoteKind(String noteKind){
        return noteKinds.get(noteKind);
    }
}
