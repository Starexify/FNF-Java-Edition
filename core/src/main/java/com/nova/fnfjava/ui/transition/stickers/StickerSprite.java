package com.nova.fnfjava.ui.transition.stickers;

import com.nova.fnfjava.graphics.FunkinSprite;

public class StickerSprite extends FunkinSprite {
    public float timing = 0f;

    public StickerSprite(float x, float y, String filePath) {
        super(x, y);
        loadTexture(filePath);
        //updateHitbox();
        updateHitboxFromCurrentFrame();
    }
}
