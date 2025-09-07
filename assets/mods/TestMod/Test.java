import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nova.fnfjava.modding.api.ScriptedModule;

public class Test implements ScriptedModule {

    @Override
    public void create() {
        System.out.println("Create called from Test");
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void dispose() {

    }
}
