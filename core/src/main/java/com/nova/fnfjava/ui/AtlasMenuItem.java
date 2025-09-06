package com.nova.fnfjava.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AtlasMenuItem extends MenuTypedList.MenuListItem {
    public TextureAtlas frames;
    public boolean ownsAtlas = false;
    public boolean centered = false;

    public AtlasMenuItem(float x, float y, String name, TextureAtlas frames, Runnable callback, boolean available) {
        super(x, y, name, callback, available);
        this.frames = frames;

        if (frames != null) {
            atlas = frames;
            ownsAtlas = true;
        }

        animation.addByPrefix("idle", name + " idle", 24); // TODO: For some reason it doesn't find the idle animation in changeAnim but somehow works?
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
            //screenCenter(Axes.X); // TODO: fix this to to center the AtlasMenuItem similarly to Funkin
            //offset.copyFrom(origin);
            //offset.set(-getOriginX(), -getOriginY());
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
        // Fix: Only dispose if we own the atlas
        if (ownsAtlas && atlas != null) {
            atlas.dispose();
            atlas = null;
        }
        frames = null;
        return super.remove();
    }
}
