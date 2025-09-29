package com.nova.fnfjava.ui.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.nova.fnfjava.text.FlxText;

public class FunkinDebugDisplay extends Group {
    public static final int UPDATE_DELAY = 100;
    public static final int INNER_RECT_DIFF = 3;
    public static final int[] OUTER_RECT_DIMENSIONS = {234, 201};
    public static final int OTHERS_OFFSET = 8;

    public ShapeRenderer shapeRenderer;

    public boolean isAdvanced = false;
    public float backgroundOpacity = 0.5f;

    public int currentFPS;
    public float deltaTimeout;
    public Array<Float> times;
    public Color color;

    public float gcMem;
    public float gcMemPeak;
    public float taskMem;
    public float taskMemPeak;

    public FlxText infoDisplay;

    public FunkinDebugDisplay(float x, float y, Color color, ShapeRenderer shapeRenderer) {
        this.setX(x);
        this.setY(y);

        this.shapeRenderer = shapeRenderer;

        this.currentFPS = 0;
        this.deltaTimeout = 0.0f;

        this.gcMem = 0.0f;
        this.gcMemPeak = 0.0f;
        this.taskMem = 0.0f;
        this.taskMemPeak = 0.0f;

        this.times = new Array<>();
        this.color = color;
        setBackgroundOpacity(0);
        setIsAdvanced(false);

        buildDebugDisplay(false);
    }

    public void buildDebugDisplay(boolean advanced) {
        createSimpleElements();
    }

    public void createSimpleElements() {
        infoDisplay = new FlxText(OTHERS_OFFSET, OTHERS_OFFSET, "");
        infoDisplay.setFormat("Monsterrat", 12, color);
        StringBuilder info = new StringBuilder();
        info.append("FPS: ").append(currentFPS).append("\n");
        info.append("GC MEM: ").append(formatBytes(gcMem * 1024 * 1024))
            .append(" / ").append(formatBytes(gcMemPeak * 1024 * 1024)).append("\n");
        info.append("TASK MEM: ").append(formatBytes(taskMem * 1024 * 1024))
            .append(" / ").append(formatBytes(taskMemPeak * 1024 * 1024));

        infoDisplay.setText(info.toString());
        this.addActor(infoDisplay);

        updateSimpleDisplay();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float deltaMs = delta * 1000f;

        if (deltaTimeout < UPDATE_DELAY) {
            deltaTimeout += deltaMs;
            return;
        }

        currentFPS = Gdx.graphics.getFramesPerSecond();

        updateMemoryInfo();

        updateSimpleDisplay();

        deltaTimeout = 0.0f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        drawBackground();
        batch.begin();
        if (infoDisplay != null) {
            float bgHeight = getCalculatedHeight();
            infoDisplay.setPosition(getX() + OTHERS_OFFSET, getY() + bgHeight * 0.5f);
            infoDisplay.draw(batch, parentAlpha);
        }
    }

    public void updateMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        gcMem = usedMemory / (1024f * 1024f);
        taskMem = maxMemory / (1024f * 1024f);

        if (gcMem > gcMemPeak) gcMemPeak = gcMem;
        if (taskMem > taskMemPeak) taskMemPeak = taskMem;
    }

    public void updateSimpleDisplay() {
        if (infoDisplay == null) return;

        StringBuilder info = new StringBuilder();
        info.append("FPS: ").append(currentFPS).append("\n");
        info.append("GC MEM: ").append(formatBytes(gcMem * 1024 * 1024))
            .append(" / ").append(formatBytes(gcMemPeak * 1024 * 1024)).append("\n");
        info.append("TASK MEM: ").append(formatBytes(taskMem * 1024 * 1024))
            .append(" / ").append(formatBytes(taskMemPeak * 1024 * 1024));

        infoDisplay.setText(info.toString());
    }

    public void drawBackground() {
        float bgWidthMultiplier = 1.0f;
        float bgHeightMultiplier = isAdvanced ? 1.0f : 0.3f;

        float bgWidth = (OUTER_RECT_DIMENSIONS[0] * bgWidthMultiplier) + (INNER_RECT_DIFF * 2);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Outer rectangle (darker)
        shapeRenderer.setColor(0x3d / 255f, 0x3f / 255f, 0x41 / 255f, backgroundOpacity);
        shapeRenderer.rect(getX(), getY(), bgWidth, getCalculatedHeight());

        // Inner rectangle (lighter)
        shapeRenderer.setColor(0x2c / 255f, 0x2f / 255f, 0x30 / 255f, backgroundOpacity);
        shapeRenderer.rect(getX() + INNER_RECT_DIFF, getY() + INNER_RECT_DIFF,
            OUTER_RECT_DIMENSIONS[0] * bgWidthMultiplier,
            OUTER_RECT_DIMENSIONS[1] * bgHeightMultiplier);

        shapeRenderer.end();
    }

    public float getCalculatedHeight() {
        float bgHeightMultiplier = isAdvanced ? 1.0f : 0.3f;
        return (OUTER_RECT_DIMENSIONS[1] * bgHeightMultiplier) + (INNER_RECT_DIFF * 2);
    }

    public void setIsAdvanced(boolean value) {
        buildDebugDisplay(value);
    }

    public void setBackgroundOpacity(float value) {
        this.backgroundOpacity = value;
    }

    public void dispose() {
        if (infoDisplay != null) {
            infoDisplay.dispose();
            infoDisplay = null;
        }
    }

    public String formatBytes(double bytes) {
        if (bytes < 1024) return String.format("%.0f B", bytes);
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024 * 1024));
        return String.format("%.1f GB", bytes / (1024 * 1024 * 1024));
    }

    public enum DebugDisplayMode {
        OFF(0),
        SIMPLE(1),
        ADVANCED(2);

        private final int value;

        DebugDisplayMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
