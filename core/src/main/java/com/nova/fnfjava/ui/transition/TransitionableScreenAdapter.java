package com.nova.fnfjava.ui.transition;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

public class TransitionableScreenAdapter extends ScreenAdapter {
    protected TransitionConfig defaultTransitionInConfig = new TransitionConfig();
    protected TransitionConfig defaultTransitionOutConfig = new TransitionConfig();

    public TransitionConfig getTransitionInConfig() {
        return defaultTransitionInConfig;
    }

    public TransitionConfig getTransitionOutConfig() {
        return defaultTransitionOutConfig;
    }

    public void transitionIn() {}

    public void transitionOut() { }

    public void setDefaultTransitions(TransitionConfig inConfig, TransitionConfig outConfig) {
        this.defaultTransitionInConfig = inConfig;
        this.defaultTransitionOutConfig = outConfig;
    }

    public void setDefaultTransitionIn(TransitionType type, float duration, Interpolation interpolation) {
        this.defaultTransitionInConfig = new TransitionConfig(type, duration, interpolation, Color.BLACK);
    }

    public void setDefaultTransitionOut(TransitionType type, float duration, Interpolation interpolation) {
        this.defaultTransitionOutConfig = new TransitionConfig(type, duration, interpolation, Color.BLACK);
    }

    public static class TransitionConfig {
        public TransitionType type;
        public float duration;
        public Interpolation interpolation;
        public Color overlayColor;

        public TransitionConfig() {
            this(TransitionType.WIPE_VERTICAL, 0.5f, Interpolation.exp10Out, Color.BLACK);
        }

        public TransitionConfig(TransitionType type, float duration, Interpolation interpolation, Color overlayColor) {
            this.type = type;
            this.duration = duration;
            this.interpolation = interpolation;
            this.overlayColor = overlayColor;
        }
    }

    public enum TransitionType {
        WIPE_VERTICAL,
        NONE
    }
}
