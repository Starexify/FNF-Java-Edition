package com.nova.fnfjava.play.stage;

import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.stage.StageData;
import com.nova.fnfjava.data.stage.StageRegistry;
import com.nova.fnfjava.group.TypedActorGroup;
import com.nova.fnfjava.play.character.BaseCharacter;

public class Stage extends TypedActorGroup implements IRegistryEntry<StageData> {
    public String id;
    public StageData stageData;

    public ObjectMap<String, StageProp> namedProps = new ObjectMap<>();
    public ObjectMap<String, BaseCharacter> characters = new ObjectMap<>();

    public Stage(String id, StageRegistry.StageEntryParams params) {
        this.id = id;
    }

    public void resetStage() {
        // Reset positions of characters.
        if (getBoyfriend() != null) {
            getBoyfriend().resetCharacter(true);
            // Reapply the camera offsets.
            StageData.StageDataCharacter stageCharData = getData().characters.bf;
            float finalScale = getBoyfriend().getBaseScale() * stageCharData.scale;
            getBoyfriend().setScale(finalScale);
            /*getBoyfriend().cameraFocusPoint.x += stageCharData.cameraOffsets[0];
            getBoyfriend().cameraFocusPoint.y += stageCharData.cameraOffsets[1];*/
        } else {
            Main.logger.setTag("Stage").warn("STAGE RESET: No boyfriend found.");
        }
        if (getGirlfriend() != null) {
            getGirlfriend().resetCharacter(true);
            // Reapply the camera offsets.
            StageData.StageDataCharacter stageCharData = getData().characters.gf;
            float finalScale = getGirlfriend().getBaseScale() * stageCharData.scale;
            getGirlfriend().setScale(finalScale);
/*            getGirlfriend().cameraFocusPoint.x += stageCharData.cameraOffsets[0];
            getGirlfriend().cameraFocusPoint.y += stageCharData.cameraOffsets[1];*/
        }
        if (getDad() != null) {
            getDad().resetCharacter(true);
            // Reapply the camera offsets.
            StageData.StageDataCharacter stageCharData = getData().characters.dad;
            float finalScale = getDad().getBaseScale() * stageCharData.scale;
            getDad().setScale(finalScale);
     /*       getDad().cameraFocusPoint.x += stageCharData.cameraOffsets[0];
            getDad().cameraFocusPoint.y += stageCharData.cameraOffsets[1];*/
        }

        // Reset positions of named props.
        for (StageData.StageDataProp dataProp : getData().props) {
            // Fetch the prop.
            StageProp prop = getNamedProp(dataProp.name);

            if (prop != null) {
                // Reset the position.
                prop.setX(dataProp.position.get(0));
                prop.setFlxY(dataProp.position.get(1));
                prop.setZIndex(dataProp.zIndex);
            }
        }

        // We can assume unnamed props are not moving.
    }

    public BaseCharacter getCharacter(String id) {
        return this.characters.get(id);
    }

    public BaseCharacter getBoyfriend(boolean pop) {
        if (pop) {
            BaseCharacter boyfriend = getCharacter("bf");

            // Remove the character from the stage.
            boyfriend.remove();
            this.characters.remove("bf");

            return boyfriend;
        } else {
            return getCharacter("bf");
        }
    }

    public BaseCharacter getBoyfriend() {
        return getBoyfriend(false);
    }

    public BaseCharacter getGirlfriend(boolean pop) {
        if (pop) {
            BaseCharacter girlfriend = getCharacter("gf");

            // Remove the character from the stage.
            girlfriend.remove();
            this.characters.remove("gf");

            return girlfriend;
        } else {
            return getCharacter("gf");
        }
    }

    public BaseCharacter getGirlfriend() {
        return getGirlfriend(false);
    }

    public BaseCharacter getDad(boolean pop) {
        if (pop) {
            BaseCharacter dad = getCharacter("dad");

            // Remove the character from the stage.
            dad.remove();
            this.characters.remove("dad");

            return dad;
        } else {
            return getCharacter("dad");
        }
    }

    public BaseCharacter getDad() {
        return getDad(false);
    }

    public StageProp getNamedProp(String name) {
        return this.namedProps.get(name);
    }

    public float getCamZoom() {
        return getData().cameraZoom != null ? getData().cameraZoom : 1.0f;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public StageData getData() {
        return this.stageData;
    }

    @Override
    public void loadData(StageData data) {
        if (data == null) throw new IllegalArgumentException("StageData cannot be null");
        this.stageData = data;
    }

    @Override
    public void destroy() {

    }

    @Override
    public String toString() {
        return "Stage{" +
            "id='" + id + '\'' +
            ", stageData=" + stageData +
            '}';
    }
}
