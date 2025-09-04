package com.nova.fnfjava;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nova.fnfjava.ui.MusicBeatState;

public class ScrollableStage extends Stage {
    private MusicBeatState parentState;

    private Actor target;
    private Vector2 cameraTarget = new Vector2();
    private Vector2 cameraPosition = new Vector2(); // scroll
    private Vector2 originalCameraPosition = new Vector2();
    private float followLerp = 0.06f;
    private boolean followEnabled = false;

    private float minX = Float.NEGATIVE_INFINITY;
    private float maxX = Float.POSITIVE_INFINITY;
    private float minY = Float.NEGATIVE_INFINITY;
    private float maxY = Float.POSITIVE_INFINITY;

    public ScrollableStage(Viewport viewport, Batch batch) {
        super(viewport, batch);

        // Store the original camera position properly
        originalCameraPosition.set(
            getCamera().position.x - getCamera().viewportWidth / 2,
            getCamera().position.y - getCamera().viewportHeight / 2
        );

        // Initialize current position to original position
        cameraPosition.set(originalCameraPosition);
        cameraTarget.set(cameraPosition);
    }

    public void follow(Actor actor, float lerp) {
        this.target = actor;
        this.followLerp = lerp;
        this.followEnabled = true;
    }

    public void follow(Actor actor) {
        follow(actor, 1.0f);
    }

    public void stopFollowing(boolean snapToTarget) {
        if (snapToTarget && target != null) {
            snapToTarget();
        }
        this.followEnabled = false;
        this.target = null;
    }

    public void snapToTarget() {
        if (target != null) {
            updateFollow();
            cameraPosition.set(cameraTarget);
        }
    }

    private void updateCameraTarget() {
        if (target != null) {
            float targetX = target.getX() + target.getWidth() / 2 - getCamera().viewportWidth / 2;
            float targetY = target.getY() + target.getHeight() / 2 - getCamera().viewportHeight / 2;
            cameraTarget.set(targetX, targetY);
        }
    }

    private void updateFollow() {
        // Apply bounds
        float clampedX = MathUtils.clamp(cameraPosition.x, minX, maxX);
        float clampedY = MathUtils.clamp(cameraPosition.y, minY, maxY);

        // Update camera
        getCamera().position.set(
            clampedX + getCamera().viewportWidth / 2,
            clampedY + getCamera().viewportHeight / 2,
            0
        );
        getCamera().update();
    }

    @Override
    public void draw() {
        Camera camera = getCamera();
        Vector3 originalPosition = new Vector3(camera.position);

        // Calculate camera offset from original position
        float offsetX = cameraPosition.x - originalCameraPosition.x;
        float offsetY = cameraPosition.y - originalCameraPosition.y;

        getBatch().begin();

        // Draw all actors
        for (Actor actor : getActors()) {
            if (actor instanceof ScrollableActor) {
                ScrollableActor scrollableActor = (ScrollableActor) actor;

                // Apply scroll factor to camera offset
                float scrollOffsetX = offsetX * scrollableActor.scrollFactorX;
                float scrollOffsetY = offsetY * scrollableActor.scrollFactorY;

                // Temporarily adjust camera position for this actor
                camera.position.set(
                    originalPosition.x + scrollOffsetX,
                    originalPosition.y + scrollOffsetY,
                    originalPosition.z
                );
                camera.update();
                getBatch().setProjectionMatrix(camera.combined);

                actor.draw(getBatch(), 1.0f);
            } else {
                // For regular actors, keep camera at original position (static)
                camera.position.set(originalPosition);
                camera.update();
                getBatch().setProjectionMatrix(camera.combined);

                actor.draw(getBatch(), 1.0f);
            }
        }

        getBatch().end();

        // Restore original camera position
        camera.position.set(originalPosition);
        camera.update();
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        if (followEnabled && target != null) {
            updateCameraTarget();
            updateLerp(delta);
        }
    }

    private void updateLerp(float delta) {
        if (followLerp >= 1.0f) {
            cameraPosition.set(cameraTarget);
        } else if (followLerp > 0f) {
            float adjustedLerp = 1f - (float)Math.pow(1f - followLerp, delta * 60f);
            cameraPosition.x = MathUtils.lerp(cameraPosition.x, cameraTarget.x, adjustedLerp);
            cameraPosition.y = MathUtils.lerp(cameraPosition.y, cameraTarget.y, adjustedLerp);
        }
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
    }

    public void addActorWithScrollFactor(Actor actor, float scrollX, float scrollY) {
        ScrollableActor wrapper = new ScrollableActor(actor);
        wrapper.setScrollFactor(scrollX, scrollY);
        addActor(wrapper);
    }

    public void setParentState(MusicBeatState parentState) {
        this.parentState = parentState;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        boolean handled = super.scrolled(amountX, amountY);
        if (!handled && parentState != null) handled = parentState.handleMouseWheel(amountY);

        return super.scrolled(amountX, amountY);
    }

    public static class ScrollableActor extends Group {
        public float scrollFactorX = 1.0f;
        public float scrollFactorY = 1.0f;

        public ScrollableActor(Actor child) {
            addActor(child);
        }

        public void setScrollFactor(float x, float y) {
            this.scrollFactorX = x;
            this.scrollFactorY = y;
        }
    }
}
