package com.nova.fnfjava.modding.api;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ScriptedModule {
    void create();

    // Additional lifecycle methods
    default void update(float delta) {}
    default void render(SpriteBatch batch) {}
    default void pause() {}
    default void dispose() {}
    default void resume() {}
}
