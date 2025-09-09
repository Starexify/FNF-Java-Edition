package com.nova.fnfjava.play.notes;

/**
 * The direction of a note.
 * This has implicit casting set up, so you can use this as an integer.
 */
public enum NoteDirection {
    LEFT(0, "left", "purple"),
    DOWN(1, "down", "blue"),
    UP(2, "up", "green"),
    RIGHT(3, "right", "red");

    private final int value;
    private final String name;
    private final String colorName;

    NoteDirection(int value, String name, String colorName) {
        this.value = value;
        this.name = name;
        this.colorName = colorName;
    }
}
