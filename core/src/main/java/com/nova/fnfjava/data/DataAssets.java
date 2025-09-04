package com.nova.fnfjava.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class DataAssets {
    public static String buildDataPath(String path) {
        return "assets/data/" + path;
    }

    public static Array<String> listDataFilesInPath(String path, String suffix) {
        FileHandle dir = Gdx.files.internal(buildDataPath(path));

        Array<String> results = new Array<>();

        for (FileHandle file : dir.list()) {
            String fileName = file.name();

            if (fileName.endsWith(suffix)) {
                String pathNoSuffix = fileName.substring(0, fileName.length() - suffix.length());
                if (!results.contains(pathNoSuffix, false)) {
                    results.add(pathNoSuffix);
                }
            }
        }

        return results;
    }

    public static Array<String> listDataFilesInPath(String path) {
        return listDataFilesInPath(path, ".json");
    }
}
