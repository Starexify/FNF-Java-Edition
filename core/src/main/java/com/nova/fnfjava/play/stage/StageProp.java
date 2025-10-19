package com.nova.fnfjava.play.stage;

import com.nova.fnfjava.graphics.FunkinSprite;

public class StageProp extends FunkinSprite {
    public String name = "";

    public StageProp(float x, float y) {
        super(x, y);
    }

    public StageProp() {
        this(0, 0);
    }
}
