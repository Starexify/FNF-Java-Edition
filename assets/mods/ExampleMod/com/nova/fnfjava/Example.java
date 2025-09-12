package com.nova.fnfjava;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nova.fnfjava.modding.api.ScriptedModule;

public class Example implements ScriptedModule {
    public void create() {
        System.out.println(getClass().getClassLoader().getName());
    }

    public void update(float delta) {

    }

    public void render(SpriteBatch batch) {
        //System.out.println("Render called from Example");
    }
}
