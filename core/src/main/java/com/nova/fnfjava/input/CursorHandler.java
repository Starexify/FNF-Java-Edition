package com.nova.fnfjava.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.nova.fnfjava.Paths;

public class CursorHandler {

    private static CursorMode currentCursorMode;
    private static boolean visible = true;

    public static final CursorParams CURSOR_DEFAULT_PARAMS = new CursorParams("cursor/cursor-default", 1.0f, 0, 0);
    public static final CursorParams CURSOR_CROSS_PARAMS = new CursorParams("cursor/cursor-cross", 1.0f, 75, 75);
    public static final CursorParams CURSOR_ERASER_PARAMS = new CursorParams("cursor/cursor-eraser", 1.0f, 30, 130);
    public static final CursorParams CURSOR_GRABBING_PARAMS = new CursorParams("cursor/cursor-grabbing", 1.0f, 13, 0);
    public static final CursorParams CURSOR_HOURGLASS_PARAMS = new CursorParams("cursor/cursor-hourglass", 1.0f, 65, 75);
    public static final CursorParams CURSOR_POINTER_PARAMS = new CursorParams("cursor/cursor-pointer", 1.0f, 13, 0);
    public static final CursorParams CURSOR_TEXT_PARAMS = new CursorParams("cursor/cursor-text", 0.2f, 54, 75);
    public static final CursorParams CURSOR_TEXT_VERTICAL_PARAMS = new CursorParams("cursor/cursor-text-vertical", 0.2f, 75, 54);
    public static final CursorParams CURSOR_ZOOM_IN_PARAMS = new CursorParams("cursor/cursor-zoom-in", 1.0f, 0, 0);
    public static final CursorParams CURSOR_ZOOM_OUT_PARAMS = new CursorParams("cursor/cursor-zoom-out", 1.0f, 0, 0);
    public static final CursorParams CURSOR_CROSSHAIR_PARAMS = new CursorParams("cursor/cursor-crosshair", 1.0f, 16, 16);
    public static final CursorParams CURSOR_CELL_PARAMS = new CursorParams("cursor/cursor-cell", 1.0f, 16, 16);
    public static final CursorParams CURSOR_SCROLL_PARAMS = new CursorParams("cursor/cursor-scroll", 0.2f, 75, 75);

    private static Cursor assetCursorDefault = null;
    private static Cursor assetCursorCross = null;
    private static Cursor assetCursorEraser = null;
    private static Cursor assetCursorGrabbing = null;
    private static Cursor assetCursorHourglass = null;
    private static Cursor assetCursorPointer = null;
    private static Cursor assetCursorText = null;
    private static Cursor assetCursorTextVertical = null;
    private static Cursor assetCursorZoomIn = null;
    private static Cursor assetCursorZoomOut = null;
    private static Cursor assetCursorCrosshair = null;
    private static Cursor assetCursorCell = null;
    private static Cursor assetCursorScroll = null;

    public static void initCursors() {
        setCursorMode(CursorMode.DEFAULT);
    }

    public static void show() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        setCursorMode(currentCursorMode);
        visible = true;
    }

    public static void hide() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        visible = false;
    }

    public static void toggle() {
        if (visible) hide();
        else show();
    }

    public static void setCursorMode(CursorMode mode) {
        if (mode != null) {
            currentCursorMode = mode;
            setCursorGraphic(currentCursorMode);
        }
    }

    public static void setCursorGraphic(CursorMode mode) {
        if (mode == null) {
            Gdx.graphics.setCursor(null);
            return;
        }

        switch (mode) {
            case DEFAULT:
                if (assetCursorDefault == null) assetCursorDefault = createCursor(CURSOR_DEFAULT_PARAMS);
                Gdx.graphics.setCursor(assetCursorDefault);
                break;

            case CROSS:
                if (assetCursorCross == null) assetCursorCross = createCursor(CURSOR_CROSS_PARAMS);
                Gdx.graphics.setCursor(assetCursorCross);
                break;

            case ERASER:
                if (assetCursorEraser == null) assetCursorEraser = createCursor(CURSOR_ERASER_PARAMS);
                Gdx.graphics.setCursor(assetCursorEraser);
                break;

            case GRABBING:
                if (assetCursorGrabbing == null) assetCursorGrabbing = createCursor(CURSOR_GRABBING_PARAMS);
                Gdx.graphics.setCursor(assetCursorGrabbing);
                break;

            case HOURGLASS:
                if (assetCursorHourglass == null) assetCursorHourglass = createCursor(CURSOR_HOURGLASS_PARAMS);
                Gdx.graphics.setCursor(assetCursorHourglass);
                break;

            case POINTER:
                if (assetCursorPointer == null) assetCursorPointer = createCursor(CURSOR_POINTER_PARAMS);
                Gdx.graphics.setCursor(assetCursorPointer);
                break;

            case TEXT:
                if (assetCursorText == null) assetCursorText = createCursor(CURSOR_TEXT_PARAMS);
                Gdx.graphics.setCursor(assetCursorText);
                break;

            case TEXT_VERTICAL:
                if (assetCursorTextVertical == null) assetCursorTextVertical = createCursor(CURSOR_TEXT_VERTICAL_PARAMS);
                Gdx.graphics.setCursor(assetCursorTextVertical);
                break;

            case ZOOM_IN:
                if (assetCursorZoomIn == null) assetCursorZoomIn = createCursor(CURSOR_ZOOM_IN_PARAMS);
                Gdx.graphics.setCursor(assetCursorZoomIn);
                break;

            case ZOOM_OUT:
                if (assetCursorZoomOut == null) assetCursorZoomOut = createCursor(CURSOR_ZOOM_OUT_PARAMS);
                Gdx.graphics.setCursor(assetCursorZoomOut);
                break;

            case CROSSHAIR:
                if (assetCursorCrosshair == null) assetCursorCrosshair = createCursor(CURSOR_CROSSHAIR_PARAMS);
                Gdx.graphics.setCursor(assetCursorCrosshair);
                break;

            case CELL:
                if (assetCursorCell == null) assetCursorCell = createCursor(CURSOR_CELL_PARAMS);
                Gdx.graphics.setCursor(assetCursorCell);
                break;

            case SCROLL:
                if (assetCursorScroll == null) assetCursorScroll = createCursor(CURSOR_SCROLL_PARAMS);
                Gdx.graphics.setCursor(assetCursorScroll);
                break;

            default:
                setCursorGraphic(null);
        }
    }

    public static Cursor createCursor(CursorParams params) {
        Pixmap originalPixmap = new Pixmap(Gdx.files.internal(Paths.image(params.graphic)));

        // Convert to RGBA8888 format if needed
        Pixmap workingPixmap;
        if (originalPixmap.getFormat() != Pixmap.Format.RGBA8888) {
            workingPixmap = new Pixmap(originalPixmap.getWidth(), originalPixmap.getHeight(), Pixmap.Format.RGBA8888);
            workingPixmap.drawPixmap(originalPixmap, 0, 0);
            originalPixmap.dispose();
        } else {
            workingPixmap = originalPixmap;
        }

        // Apply scaling if needed
        Pixmap scaledPixmap;
        int scaledHotspotX = params.offsetX;
        int scaledHotspotY = params.offsetY;

        if (params.scale != 1.0f) {
            int scaledWidth = Math.max(1, Math.round(workingPixmap.getWidth() * params.scale));
            int scaledHeight = Math.max(1, Math.round(workingPixmap.getHeight() * params.scale));

            scaledPixmap = scalePixmap(workingPixmap, scaledWidth, scaledHeight);

            // Scale hotspot coordinates
            scaledHotspotX = Math.round(params.offsetX * params.scale);
            scaledHotspotY = Math.round(params.offsetY * params.scale);

            if (workingPixmap != originalPixmap) workingPixmap.dispose();
            workingPixmap = scaledPixmap;
        }

        // Check if dimensions are power of two, if not, resize to nearest power of two
        int newWidth = nextPowerOfTwo(workingPixmap.getWidth());
        int newHeight = nextPowerOfTwo(workingPixmap.getHeight());

        Pixmap cursorPixmap;
        if (newWidth != workingPixmap.getWidth() || newHeight != workingPixmap.getHeight()) {
            // Need to resize to power of two dimensions
            cursorPixmap = new Pixmap(newWidth, newHeight, Pixmap.Format.RGBA8888);
            cursorPixmap.setColor(0, 0, 0, 0); // Transparent background
            cursorPixmap.fill();

            // Center the scaled image in the new power-of-two canvas
            int offsetX = (newWidth - workingPixmap.getWidth()) / 2;
            int offsetY = (newHeight - workingPixmap.getHeight()) / 2;
            cursorPixmap.drawPixmap(workingPixmap, offsetX, offsetY);

            // Adjust hotspot coordinates to account for centering
            int adjustedHotspotX = scaledHotspotX + offsetX;
            int adjustedHotspotY = scaledHotspotY + offsetY;

            workingPixmap.dispose();

            Cursor cursor = Gdx.graphics.newCursor(cursorPixmap, adjustedHotspotX, adjustedHotspotY);
            cursorPixmap.dispose();
            return cursor;
        } else {
            // Dimensions are already power of two
            Cursor cursor = Gdx.graphics.newCursor(workingPixmap, scaledHotspotX, scaledHotspotY);
            workingPixmap.dispose();
            return cursor;
        }
    }

    private static Pixmap scalePixmap(Pixmap original, int newWidth, int newHeight) {
        Pixmap scaled = new Pixmap(newWidth, newHeight, Pixmap.Format.RGBA8888);
        scaled.setColor(0, 0, 0, 0); // Transparent background
        scaled.fill();

        // Simple nearest-neighbor scaling
        float scaleX = (float) original.getWidth() / newWidth;
        float scaleY = (float) original.getHeight() / newHeight;

        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                int srcX = Math.min((int) (x * scaleX), original.getWidth() - 1);
                int srcY = Math.min((int) (y * scaleY), original.getHeight() - 1);
                int pixel = original.getPixel(srcX, srcY);
                scaled.drawPixel(x, y, pixel);
            }
        }

        return scaled;
    }

    private static int nextPowerOfTwo(int value) {
        if (value <= 1) return 1;
        return Integer.highestOneBit(value - 1) << 1;
    }

    public static void dispose() {
        if (assetCursorDefault != null) assetCursorDefault.dispose();
        if (assetCursorPointer != null) assetCursorPointer.dispose();
        if (assetCursorText != null) assetCursorText.dispose();
        if (assetCursorCrosshair != null) assetCursorCrosshair.dispose();
    }

    public enum CursorMode {
        DEFAULT,
        CROSS,
        ERASER,
        GRABBING,
        HOURGLASS,
        POINTER,
        TEXT,
        TEXT_VERTICAL,
        ZOOM_IN,
        ZOOM_OUT,
        CROSSHAIR,
        CELL,
        SCROLL
    }

    public record CursorParams(String graphic, float scale, int offsetX, int offsetY) {}
}
