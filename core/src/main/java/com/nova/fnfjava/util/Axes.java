package com.nova.fnfjava.util;

public enum Axes {
    X, Y, XY, NONE;

    public boolean hasX() {
        return this == X || this == XY;
    }

    public boolean hasY() {
        return this == Y || this == XY;
    }
}
