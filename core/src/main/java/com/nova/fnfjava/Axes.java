package com.nova.fnfjava;

public enum Axes {
    X, Y, XY, NONE;

    public boolean hasX() {
        return this == X || this == XY;
    }

    public boolean hasY() {
        return this == Y || this == XY;
    }
}
