package com.nova.fnfjava.play.stage;

import com.nova.fnfjava.data.IRegistryEntry;
import com.nova.fnfjava.data.stage.StageData;
import com.nova.fnfjava.data.stage.StageRegistry;
import com.nova.fnfjava.group.TypedActorGroup;

public class Stage extends TypedActorGroup implements IRegistryEntry<StageData> {
    public String id;
    public StageData stageData;

    public Stage(String id, StageRegistry.StageEntryParams params) {
        this.id = id;
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
