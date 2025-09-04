package com.nova.fnfjava.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class DataAssets {
    public static String buildDataPath(String path) {
        return "assets/data/" + path;
    }

    public static Array<String> listDataFilesInPath(String path, String suffix) {
        Array<String> results = new Array<>();
        listFilesRecursive(Gdx.files.internal(buildDataPath(path)), suffix, results, "");
        return results;
    }

    private static void listFilesRecursive(FileHandle dir, String suffix, Array<String> results, String relativePath) {
        if (dir.isDirectory()) {
            for (FileHandle file : dir.list()) {
                String newRelativePath = relativePath.isEmpty() ? file.name() : relativePath + "/" + file.name();
                listFilesRecursive(file, suffix, results, newRelativePath);
            }
        } else {
            if (dir.name().endsWith(suffix)) {
                // Remove suffix from the relative path
                String pathNoSuffix = relativePath.substring(0, relativePath.length() - suffix.length());
                if (!results.contains(pathNoSuffix, false)) results.add(pathNoSuffix);
            }
        }
    }

    public static Array<String> listDataFilesInPath(String path) {
        return listDataFilesInPath(path, ".json");
    }
}
