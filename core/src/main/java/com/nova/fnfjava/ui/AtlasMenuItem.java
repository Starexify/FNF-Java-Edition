package com.nova.fnfjava.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasMenuItem extends MenuListItem {
    public TextureAtlas frames;

    public boolean centered = false;

    public AtlasMenuItem(float x, float y, String name, TextureAtlas frames, Runnable callback, boolean available) {
        super(x, y, name, callback, available);
        this.frames = frames;

        if (frames != null) atlas = frames;
        animation.addByPrefix("idle", name + " idle", 24);
        animation.addByPrefix("selected", name + " selected", 24);
    }

    public AtlasMenuItem(String name, TextureAtlas frames, Runnable callback) {
        this(0, 0, name, frames, callback, true);
    }

    public void changeAnim(String animName) {
        animation.play(animName);
        updateHitboxFromCurrentFrame();

        if (centered) {
            centerOrigin();
            //offset.copyFrom(origin);
        }
    }

    @Override
    public void idle() {
        changeAnim("idle");
    }

    @Override
    public void select() {
        changeAnim("selected");
    }

    @Override
    public boolean remove() {
        atlas.dispose();
        return super.remove();
    }
}
