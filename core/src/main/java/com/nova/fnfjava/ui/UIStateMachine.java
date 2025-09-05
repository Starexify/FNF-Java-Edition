package com.nova.fnfjava.ui;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Main;

/**
 * Note: Not to be confust with FlxState or FlxSubState!
 * State as in the design pattern!
 * https://refactoring.guru/design-patterns/state
 */
public class UIStateMachine {
    public UIState currentState = UIState.IDLE;
    public UIState previousState = UIState.IDLE;
    public ObjectMap<UIState, Array<UIState>> validTransitions;
    public final Signal<UIStateTransition> onStateChanged = new Signal<>();

    public record UIStateTransition(UIState from, UIState to) { }

    public UIStateMachine() {
        this(null);
    }

    public UIStateMachine(ObjectMap<UIState, Array<UIState>> transitions) {
        if (transitions != null) {
            validTransitions = transitions;
        } else {
            validTransitions = new ObjectMap<>();
            validTransitions.put(UIState.IDLE, Array.with(
                UIState.INTERACTING, UIState.ENTERING, UIState.EXITING, UIState.DISABLED
            ));
            validTransitions.put(UIState.ENTERING, Array.with(
                UIState.IDLE, UIState.EXITING, UIState.DISABLED, UIState.INTERACTING
            ));
            validTransitions.put(UIState.INTERACTING, Array.with(
                UIState.IDLE, UIState.ENTERING, UIState.EXITING, UIState.DISABLED
            ));
            validTransitions.put(UIState.EXITING, Array.with(
                UIState.IDLE
            ));
            validTransitions.put(UIState.DISABLED, Array.with(
                UIState.IDLE
            ));
        }
    }

    public boolean canTransition(UIState from, UIState to) {
        if (from != currentState) return false;

        Array<UIState> allowedStates = validTransitions.get(from);
        return allowedStates != null && allowedStates.contains(to, false);
    }

    public boolean transition(UIState newState) {
        // Allow same-state transitions (idempotent)
        if (currentState == newState) {
            Main.logger.setTag(this.getClass().getSimpleName()).debug("State transition: " + currentState + " -> " + newState + " (no change)");
            return true;
        }

        if (!canTransition(currentState, newState)) {
            Main.logger.setTag(this.getClass().getSimpleName()).debug("Invalid state transition: " + currentState + " -> " + newState);
            return false;
        }

        previousState = currentState;
        currentState = newState;

        Main.logger.setTag(this.getClass().getSimpleName()).debug("State transition: " + previousState + " -> " + currentState);

        // Notify listeners using Signal
        onStateChanged.dispatch(new UIStateTransition(previousState, currentState));

        return true;
    }

    public void reset() {
        previousState = currentState;
        currentState = UIState.IDLE;
    }

    public boolean is(UIState state) {
        return currentState == state;
    }

    public boolean canInteract() {
        // Entering is an enabled state since we want to be able to interact even during screen transitions
        return currentState == UIState.IDLE || currentState == UIState.ENTERING;
    }

    /**
     * Simple state machine for UI components
     * Replaces scattered boolean flags with clean state management
     */
    public enum UIState {
        IDLE,
        INTERACTING,
        ENTERING,
        EXITING,
        DISABLED
    }
}
