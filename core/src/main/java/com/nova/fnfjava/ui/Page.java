package com.nova.fnfjava.ui;

public class Page {
}

record PageName(String value) {
    @Override
    public String toString() {
        return value;
    }
}
