package com.nova.fnfjava.modding.api.metadata;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Main;

import java.util.Set;

public class ModMetadata {
    public String modId;
    public String title;
    public String version;
    public Array<String> mainClasses;
    public String mainClass;
    public Set<String> authors;
    public int priority;
    public ObjectMap<String, String> dependencies;
    public ObjectMap<String, String> optionalDependencies;

    public ModMetadata() {
        this.priority = 0;
        this.dependencies = new ObjectMap<>();
        this.optionalDependencies = new ObjectMap<>();
        this.mainClasses = new Array<>();
    }

    public Array<String> getEntryPoints() {
        if (mainClasses != null && mainClasses.size > 0) {
            return mainClasses;
        }
        // Fallback to single mainClass
        if (mainClass != null && !mainClass.isBlank()) {
            Array<String> fallback = new Array<>();
            fallback.add(mainClass);
            return fallback;
        }
        return new Array<>();
    }

    public boolean hasDependency(String modId) {
        return dependencies.containsKey(modId);
    }

    public boolean hasOptionalDependency(String modId) {
        return optionalDependencies.containsKey(modId);
    }

    public boolean validate() {
        if (modId == null || modId.isBlank()) {
            Main.logger.setTag("MODDING").error("Mod ID cannot be null or blank!");
            return false;
        }
        if (title == null || title.isBlank()) {
            Main.logger.setTag("MODDING").error("Mod title cannot be null or blank!");
            return false;
        }
        if (version == null || version.isBlank()) {
            Main.logger.setTag("MODDING").error("Mod version cannot be null or blank!");
            return false;
        }
        if (mainClass == null || mainClass.isBlank()) {
            Main.logger.setTag("MODDING").error("Main class cannot be null or blank!");
            return false;
        }

        // Ensure arrays are never null
        if (dependencies == null) dependencies = new ObjectMap<>();
        if (optionalDependencies == null) optionalDependencies = new ObjectMap<>();

        return true;
    }

    @Override
    public String toString() {
        return "ModMetadata{" +
            "modId='" + modId + '\'' +
            ", title='" + title + '\'' +
            ", version='" + version + '\'' +
            ", mainClasses=" + mainClasses +
            ", mainClass='" + mainClass + '\'' +
            ", authors=" + authors +
            ", priority=" + priority +
            ", dependencies=" + dependencies +
            ", optionalDependencies=" + optionalDependencies +
            '}';
    }
}
