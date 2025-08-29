package com.nova.fnfjava.input.actions;

public class GameAction {
    private boolean currentState = false;
    private boolean previousState = false;

    public void update(boolean pressed) {
        previousState = currentState;
        currentState = pressed;
    }

    public boolean isPressed() {
        return currentState;
    }

    public boolean isJustPressed() {
        return currentState && !previousState;
    }

    public boolean isJustReleased() {
        return !currentState && previousState;
    }
}
