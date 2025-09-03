package com.nova.fnfjava.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nova.fnfjava.Assets;

public class ImageUtil {
    public static Image createColored(float width, float height, Color color) {
        // Use your existing white pixel from Assets
        Texture whitePixel = Assets.createColoredTexture(1, 1, Color.WHITE);

        Image image = new Image(new TextureRegionDrawable(new TextureRegion(whitePixel)));
        image.setSize(width, height);
        image.setColor(color);

        return image;
    }

    public static Image blackScreen() {
        return createColored(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Color.BLACK);
    }

    public static Image redRectangle(float width, float height) {
        return createColored(width, height, Color.RED);
    }
}
