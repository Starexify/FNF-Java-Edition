package com.nova.fnfjava;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ScrollableComponent {
    public Vector2 scrollFactor = new Vector2(1, 1);
    public Vector2 worldPosition = new Vector2();
    private Actor actor;

    public ScrollableComponent(Actor actor) {
        this.actor = actor;
        this.worldPosition.set(actor.getX(), actor.getY());
    }

    public void setScrollFactor(float x, float y) {
        scrollFactor.set(x, y);
    }

    public void setWorldPosition(float x, float y) {
        worldPosition.set(x, y);
    }

    public void updateScreenPosition(Camera camera) {
        float screenX = worldPosition.x - (camera.position.x * scrollFactor.x);
        float screenY = worldPosition.y - (camera.position.y * scrollFactor.y);
        actor.setPosition(screenX, screenY);
    }

    public Vector2 getScrollFactor() { return scrollFactor; }
    public Vector2 getWorldPosition() { return worldPosition; }
}
