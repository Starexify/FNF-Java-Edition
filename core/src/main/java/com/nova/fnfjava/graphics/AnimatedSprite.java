package com.nova.fnfjava.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.nova.fnfjava.Assets;
import com.nova.fnfjava.util.Axes;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.animation.AnimationController;

public class AnimatedSprite extends Actor {
    public AnimationController animation;

    public boolean active = true;
    public boolean dirty = true;

    public int frameWidth = 0, frameHeight = 0;

    public TextureRegion frame;
    public TextureAtlas.AtlasSprite sprite;
    public TextureAtlas atlas;

    public Vector2 offset = new Vector2();

    public AnimatedSprite(float x, float y) {
        animation = new AnimationController(this);
        setPosition(x, y, Align.bottom);
    }

    public AnimatedSprite() {
        this(0, 0);
    }

    /**
     * Load a graphic/texture similar to FlxSprite's loadGraphic method
     *
     * @param graphicPath Path to the texture file
     * @param animated    Whether the graphic should be treated as an animated spritesheet
     * @param frameWidth  Width of individual frames (0 = auto-detect)
     * @param frameHeight Height of individual frames (0 = auto-detect)
     * @return This AnimatedSprite for method chaining
     */
    public AnimatedSprite loadGraphic(String graphicPath, boolean animated, int frameWidth, int frameHeight) {
        Texture texture = Assets.getTexture(graphicPath);
        if (texture == null) return this;

        if (frameWidth == 0) {
            frameWidth = animated ? texture.getHeight() : texture.getWidth();
            frameWidth = Math.min(frameWidth, texture.getWidth());
        } else if (frameWidth > texture.getWidth()) {
            Main.logger.setTag(this.getClass().getSimpleName()).warn("frameWidth " + frameWidth + " is larger than texture width " + texture.getWidth());
        }

        if (frameHeight == 0) {
            frameHeight = animated ? frameWidth : texture.getHeight();
            frameHeight = Math.min(frameHeight, texture.getHeight());
        } else if (frameHeight > texture.getHeight()) {
            Main.logger.setTag(this.getClass().getSimpleName()).warn("frameHeight " + frameHeight + " is larger than texture height " + texture.getHeight());
        }

        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        if (animated) {
            // Create frames for animation
            atlas = createAtlasFromTexture(texture, frameWidth, frameHeight);
        } else {
            // Single frame
            frame = new TextureRegion(texture, 0, 0, frameWidth, frameHeight);
            atlas = null;
        }

        setSize(frameWidth, frameHeight);

        return this;
    }

    public AnimatedSprite loadGraphic(String graphicPath, boolean animated, int frameWidth) {
        return loadGraphic(graphicPath, animated, frameWidth, 0);
    }

    public AnimatedSprite loadGraphic(String graphicPath) {
        return loadGraphic(graphicPath, false, 0, 0);
    }

    public AnimatedSprite makeGraphic(int width, int height, Color color, boolean unique, String key) {
        Texture texture = Assets.createColoredTexture(width, height, color, unique, key);
        this.frameWidth = width;
        this.frameHeight = height;
        this.frame = new TextureRegion(texture, 0, 0, width, height);
        this.atlas = null;

        setSize(width, height);
        centerOrigin();

        return this;
    }

    public AnimatedSprite makeGraphic(int width, int height, Color color) {
        return makeGraphic(width, height, color, false, null);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = getCurrentDisplayFrame();
        if (currentFrame == null) return;

        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        float drawX = getX() - offset.x;
        float drawY = getY() - offset.y;
        float rotationToApply = getRotation();

        if (currentFrame instanceof TextureAtlas.AtlasRegion) {
            TextureAtlas.AtlasRegion atlasFrame = (TextureAtlas.AtlasRegion) currentFrame;
            drawX += atlasFrame.offsetX;
            drawY += atlasFrame.offsetY;

            // Check if this atlas region was rotated
            if (atlasFrame.rotate) {
                rotationToApply += 90f;
                Main.logger.info("Applying rotation: " + rotationToApply + " to frame: " + atlasFrame.name);
                Main.logger.info("Frame dimensions: " + currentFrame.getRegionWidth() + "x" + currentFrame.getRegionHeight());
            }
        }

        batch.draw(currentFrame, drawX, drawY, getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), rotationToApply);

        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void act(float delta) {
        if (!active) return;
        super.act(delta);
        animation.update(delta);

        TextureRegion currentFrame = getCurrentDisplayFrame();
        if (currentFrame != null && (getWidth() == 0 || getHeight() == 0)) {
            setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
            dirty = true;
        }
    }

    public AnimatedSprite screenCenter(Axes axes) {
        if (axes.hasX()) setX((Gdx.graphics.getWidth() - getWidth()) / 2f);
        if (axes.hasY()) setY((Gdx.graphics.getHeight() - getHeight()) / 2f);
        return this;
    }

    public TextureRegion getCurrentDisplayFrame() {
        TextureRegion animFrame = animation.getCurrentFrame();
        if (animFrame != null) return animFrame;

        return frame;
    }

    public TextureAtlas createAtlasFromTexture(Texture texture, int frameWidth, int frameHeight) {
        TextureAtlas atlas = new TextureAtlas();
        int cols = texture.getWidth() / frameWidth;
        int rows = texture.getHeight() / frameHeight;
        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TextureAtlas.AtlasRegion region = new TextureAtlas.AtlasRegion(texture,
                    col * frameWidth, row * frameHeight, frameWidth, frameHeight);
                region.index = index++;
                region.name = "frame";
                atlas.getRegions().add(region);
            }
        }

        return atlas;
    }

    public void updateHitbox() {
        if (frameWidth > 0 && frameHeight > 0) {
            float newWidth = Math.abs(getScaleX()) * frameWidth;
            float newHeight = Math.abs(getScaleY()) * frameHeight;

            setSize(newWidth, newHeight);

            // Calculate offset like FlxSprite
            offset.set(-0.5f * (newWidth - frameWidth), -0.5f * (newHeight - frameHeight));
            centerOrigin();
        }
    }

    // Similar to updateHitbox till I fix updateHitbox to be similar to Flixel's one
    public void updateHitboxFromCurrentFrame() {
        TextureRegion currentFrame = getCurrentDisplayFrame();
        if (currentFrame != null) {
            int currentFrameWidth = currentFrame.getRegionWidth();
            int currentFrameHeight = currentFrame.getRegionHeight();

            // Update size based on current frame and scale
            float newWidth = Math.abs(getScaleX()) * currentFrameWidth;
            float newHeight = Math.abs(getScaleY()) * currentFrameHeight;

            setSize(newWidth, newHeight);

            // Calculate offset like FlxSprite does
            offset.set(-0.5f * (newWidth - currentFrameWidth), -0.5f * (newHeight - currentFrameHeight));

            // Center origin on the frame
            setOrigin(currentFrameWidth * 0.5f, currentFrameHeight * 0.5f);
        }
    }

    public void calcFrame(boolean force) {
        if (!dirty && !force) return;

    }

    public void centerOrigin() {
        setOrigin(frameWidth * 0.5f, frameHeight * 0.5f);
    }

    public void resetHelpers() {
        resetFrameSize();
        centerOrigin();
        dirty = true;
    }

    public void resetFrameSize() {
        if (atlas != null && atlas.getRegions().size > 0) {
            // Get the first frame like HaxeFlixel does
            TextureAtlas.AtlasRegion firstFrame = atlas.getRegions().first();
            frameWidth = firstFrame.getRegionWidth();
            frameHeight = firstFrame.getRegionHeight();

            // Set the current frame to the first one
            frame = firstFrame;

            resetSizeFromFrame();
        }
    }

    public void resetSizeFromFrame() {
        if (frameWidth > 0 && frameHeight > 0) setSize(frameWidth, frameHeight);
    }

    // Getter and Setters
    public void setAntialiasing(boolean bool) {
        if (bool)
            for (Texture texture : atlas.getTextures()) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void setAtlas(String path) {
        atlas = Assets.getAtlas(path);
        if (atlas != null) resetHelpers();
    }

    public void addX(float amount) {
        setX(getX() + amount);
    }

    public void addY(float amount) {
        setY(getY() + amount);
    }

    public void setFlxY(float flxY) {
        float libgdxY = Gdx.graphics.getHeight() - flxY - getHeight();
        setY(libgdxY, Align.bottom);
    }
}
