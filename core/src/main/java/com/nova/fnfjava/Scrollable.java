package com.nova.fnfjava;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public interface Scrollable {
    Vector2 getScrollFactor();
    void setScrollFactor(float x, float y);
    Vector2 getWorldPosition();
    void setWorldPosition(float x, float y);
    void updateScreenPosition(Camera camera);
}
