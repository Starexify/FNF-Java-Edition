package com.nova.fnfjava.data.stage;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.nova.fnfjava.data.BaseRegistry;
import com.nova.fnfjava.play.stage.Stage;
import io.vavr.control.Either;

public class StageRegistry extends BaseRegistry<Stage, StageData, StageRegistry.StageEntryParams> {
    public static final StageRegistry instance = new StageRegistry();

    public StageRegistry() {
        super("STAGE", "stages", Stage::new);
    }

    @Override
    public void setupParser() {
        super.setupParser();

        parser.setSerializer(Either.class, new Json.Serializer<>() {
            @Override
            public void write(Json json, Either object, Class knownType) {
                if (object.isLeft()) {
                    json.writeValue(object.getLeft());
                } else {
                    json.writeArrayStart();
                    for (Float f : (Array<Float>) object.get()) json.writeValue(f);

                    json.writeArrayEnd();
                }
            }

            @Override
            public Either read(Json json, JsonValue jsonData, Class type) {
                if (jsonData.isArray()) {
                    Array<Float> floats = new Array<>();
                    for (JsonValue v = jsonData.child; v != null; v = v.next) floats.add(v.asFloat());

                    return Either.right(floats);
                } else {
                    return Either.left(jsonData.asFloat());
                }
            }
        });
    }

    @Override
    public StageData parseEntryData(String id) {
        return parseJsonData(id, StageData.class);
    }

    @Override
    public StageEntryParams getDefaultParams(String id, StageData data) {
        return new StageEntryParams();
    }

    public record StageEntryParams() {}
}
