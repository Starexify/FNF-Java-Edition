package com.nova.fnfjava.modding.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.nova.fnfjava.Main;
import com.nova.fnfjava.modding.api.LoadedMod;
import com.nova.fnfjava.modding.api.ModLoader;
import com.nova.fnfjava.modding.api.metadata.ModMetadata;
import com.nova.fnfjava.modding.api.ScriptedModule;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class FunkyModLoader implements ModLoader {
    public final Array<LoadedModRecord> loadedMods = new Array<>();
    public final ObjectMap<String, LoadedModRecord> modRegistry = new ObjectMap<>();
    public Json json = new Json();

    public FunkyModLoader() {
        json.setIgnoreUnknownFields(true);
    }

    public record LoadedModRecord(ModMetadata metadata, Array<ScriptedModule> modules, ModType type, URLClassLoader classLoader, long lastModified, FileHandle sourceFile) implements LoadedMod {
        @Override
        public ModMetadata getMetadata() { return metadata; }

        @Override
        public List<Path> getRootPaths() {
            return List.of(type == ModType.JAR ?
                Paths.get("mods/" + metadata.modId + ".jar") :
                Paths.get("mods/" + metadata.modId));
        }

        public boolean hasChanged() {
            return sourceFile.file().lastModified() > lastModified;
        }

        public void dispose() {
            // Updated to work with Array
            for (ScriptedModule module : modules) {
                try {
                    module.dispose();
                } catch (Exception e) {
                    Main.logger.setTag("ModLoader").error("Error disposing module in " + metadata.modId, e);
                }
            }

            try {
                if (classLoader != null) classLoader.close();
            } catch (Exception e) {
                Main.logger.setTag("ModLoader").error("Error closing classloader for " + metadata.modId, e);
            }
        }
    }

    public void loadAllModsWithProgress(ModProgressCallback callback) {
        Main.logger.setTag("FunkyModLoader").info("Starting mod loading...");

        FileHandle modsDir = Gdx.files.local("mods/");
        if (!modsDir.exists()) {
            modsDir.mkdirs();
            callback.onProgress("", 1.0f, "No mods found - created directory");
            return;
        }

        Array<ModCandidate> candidates = new Array<>();
        ObjectMap<String, ModCandidate> seenIds = new ObjectMap<>();

        callback.onProgress("", 0.1f, "Scanning mod directory");

        // Scan for mods
        FileHandle[] files = modsDir.list();
        for (int i = 0; i < files.length; i++) {
            FileHandle fileHandle = files[i];
            float scanProgress = 0.1f + (0.2f * i / files.length);

            if (fileHandle.extension().equals("jar")) {
                callback.onProgress(fileHandle.name(), scanProgress, "Scanning JAR");
                populateModCandidates(candidates, seenIds, fileHandle, ModType.JAR);
            }
            if (fileHandle.isDirectory()) {
                callback.onProgress(fileHandle.name(), scanProgress, "Scanning directory");
                populateModCandidates(candidates, seenIds, fileHandle, ModType.SOURCE);
            }
        }

        // Load mods
        for (int i = 0; i < candidates.size; i++) {
            ModCandidate candidate = candidates.get(i);
            float loadProgress = 0.3f + (0.6f * i / candidates.size);

            try {
                callback.onProgress(candidate.metadata.title, loadProgress, "Loading");
                LoadedModRecord loadedMod = loadMod(candidate);
                if (loadedMod != null) {
                    loadedMods.add(loadedMod);
                    modRegistry.put(loadedMod.getMetadata().modId, loadedMod);
                    callback.onProgress(candidate.metadata.title, loadProgress + 0.3f, "Loaded successfully");
                }
            } catch (Exception e) {
                callback.onProgress(candidate.metadata.title, loadProgress, "Failed to load");
                Main.logger.setTag("FunkyModLoader").error("Failed to load mod: " + candidate.metadata.modId, e);
            }
        }

        // Initialize mods
        for (int i = 0; i < loadedMods.size; i++) {
            LoadedModRecord mod = loadedMods.get(i);
            float initProgress = 0.9f + (0.1f * i / loadedMods.size);

            try {
                callback.onProgress(mod.getMetadata().title, initProgress, "Initializing");
                mod.modules.forEach(module -> {
                    try {
                        module.create();
                    } catch (Exception e) {
                        Main.logger.setTag("FunkyModLoader").error("Failed to initialize module in " + mod.getMetadata().modId, e);
                    }
                });
            } catch (Exception e) {
                callback.onProgress(mod.getMetadata().title, initProgress, "Initialization failed");
                Main.logger.setTag("FunkyModLoader").error("Failed to initialize mod: " + mod.getMetadata().modId, e);
            }
        }

        callback.onProgress("", 1.0f, "Complete");
        Main.logger.setTag("FunkyModLoader").info("Loaded " + loadedMods.size + " mods");
    }

    @Override
    public void reloadChangedMods() {
        Main.logger.setTag("FunkyModLoader").info("Checking for changed mods...");

        Array<LoadedModRecord> toReload = new Array<>();

        // Check which mods have changed
        for (LoadedModRecord mod : loadedMods) {
            if (mod.hasChanged()) {
                toReload.add(mod);
            }
        }

        if (toReload.size == 0) {
            Main.logger.setTag("FunkyModLoader").info("No mod changes detected");
            return;
        }

        // Reload changed mods
        for (LoadedModRecord oldMod : toReload) {
            try {
                Main.logger.setTag("FunkyModLoader").info("Reloading mod: " + oldMod.metadata().title);

                // Dispose old mod
                oldMod.dispose();
                loadedMods.removeValue(oldMod, true);
                modRegistry.remove(oldMod.metadata().modId);

                // Load new version
                ModCandidate candidate = createCandidateFromFile(oldMod.sourceFile(), oldMod.type());
                if (candidate != null) {
                    LoadedModRecord newMod = loadMod(candidate);
                    if (newMod != null) {
                        loadedMods.add(newMod);
                        modRegistry.put(newMod.metadata().modId, newMod);

                        // Initialize new mod
                        for (ScriptedModule module : newMod.modules()) {
                            module.create();
                        }
                    }
                }

            } catch (Exception e) {
                Main.logger.setTag("FunkyModLoader").error("Failed to reload mod: " + oldMod.metadata().modId, e);
            }
        }

        Main.logger.setTag("FunkyModLoader").info("Reloaded " + toReload.size + " mods");
    }

    @Override
    public void forceReloadAllMods() {
        Main.logger.setTag("FunkyModLoader").info("Force reloading all mods...");

        // Store file info before disposal
        Array<FileHandle> modFiles = new Array<>();
        Array<ModType> modTypes = new Array<>();

        for (LoadedModRecord mod : loadedMods) {
            modFiles.add(mod.sourceFile());
            modTypes.add(mod.type());
        }

        // Dispose all current mods
        dispose();

        // Reload from files
        for (int i = 0; i < modFiles.size; i++) {
            FileHandle file = modFiles.get(i);
            ModType type = modTypes.get(i);

            ModCandidate candidate = createCandidateFromFile(file, type);
            if (candidate != null) {
                try {
                    LoadedModRecord newMod = loadMod(candidate);
                    if (newMod != null) {
                        loadedMods.add(newMod);
                        modRegistry.put(newMod.metadata().modId, newMod);
                    }
                } catch (Exception e) {
                    Main.logger.setTag("FunkyModLoader").error("Failed to reload mod from: " + file.path(), e);
                }
            }
        }

        // Initialize all mods
        for (LoadedModRecord mod : loadedMods) {
            try {
                for (ScriptedModule module : mod.modules()) {
                    module.create();
                }
            } catch (Exception e) {
                Main.logger.setTag("FunkyModLoader").error("Failed to initialize reloaded mod: " + mod.metadata().modId, e);
            }
        }
    }

    private ModCandidate createCandidateFromFile(FileHandle file, ModType type) {
        ModMetadata metadata = switch (type) {
            case JAR -> loadMetadataFromJar(file);
            case SOURCE -> loadMetadataFromSource(file);
        };

        return metadata != null ? new ModCandidate(metadata, file, type) : null;
    }

    public void populateModCandidates(Array<ModCandidate> candidates, ObjectMap<String, ModCandidate> seenIds, FileHandle fileHandle, ModType modType) {
        ModMetadata metadata = switch (modType) {
            case JAR -> loadMetadataFromJar(fileHandle);
            case SOURCE -> loadMetadataFromSource(fileHandle);
        };

        if (metadata != null) {
            if (seenIds.containsKey(metadata.modId)) {
                ModCandidate existing = seenIds.get(metadata.modId);
                handleDuplicateModId(metadata, fileHandle, modType, existing);
                return;
            }
            ModCandidate candidate = new ModCandidate(metadata, fileHandle, modType);
            candidates.add(candidate);
            seenIds.put(metadata.modId, candidate);
        }
    }

    public LoadedModRecord loadMod(ModCandidate candidate) throws Exception {
        return switch (candidate.type) {
            case JAR -> loadFromJar(candidate);
            case SOURCE -> loadFromSource(candidate);
        };
    }

    private LoadedModRecord loadFromJar(ModCandidate candidate) throws Exception {
        URL[] urls = {candidate.file.file().toURI().toURL()};
        URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());

        Array<ScriptedModule> modules = loadModules(candidate.metadata.getEntryPoints(), classLoader);

        return new LoadedModRecord(
            candidate.metadata, modules, candidate.type,
            classLoader, candidate.file.file().lastModified(), candidate.file
        );
    }

    private LoadedModRecord loadFromSource(ModCandidate candidate) throws Exception {
        File compiledJar = compileSourceToJar(candidate);

        URL[] urls = {compiledJar.toURI().toURL()};
        URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());

        Array<ScriptedModule> modules = loadModules(candidate.metadata.getEntryPoints(), classLoader);

        return new LoadedModRecord(
            candidate.metadata, modules, candidate.type,
            classLoader, candidate.file.file().lastModified(), candidate.file
        );
    }

    private Array<ScriptedModule> loadModules(Array<String> entryPoints, URLClassLoader classLoader) throws Exception {
        Array<ScriptedModule> modules = new Array<>();
        for (String className : entryPoints) {
            Class<?> modClass = classLoader.loadClass(className);
            ScriptedModule module = (ScriptedModule) modClass.getDeclaredConstructor().newInstance();
            modules.add(module);
        }
        return modules;
    }

    private File compileSourceToJar(ModCandidate candidate) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("No system Java compiler found. Are you running a JDK?");
        }

        // Compile
        File tempDir = new File(Gdx.files.local("mods/temp/" + candidate.metadata.modId).path());
        tempDir.mkdirs();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            List<File> javaFiles = collectJavaFiles(candidate.file.file());
            if (javaFiles.isEmpty()) {
                throw new RuntimeException("No Java files found in " + candidate.file.path());
            }

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);
            boolean success = compiler.getTask(null, fileManager, null,
                List.of("-d", tempDir.getAbsolutePath()), null, compilationUnits).call();

            if (!success) {
                throw new RuntimeException("Compilation failed for mod: " + candidate.metadata.modId);
            }
        }

        // Create JAR
        File jarFile = new File(Gdx.files.local("mods/compiled/" + candidate.metadata.modId + ".jar").path());
        jarFile.getParentFile().mkdirs();
        createJarFromCompiledClasses(tempDir, jarFile, candidate.file);

        deleteDirectory(tempDir);
        return jarFile;
    }

    public List<File> collectJavaFiles(File dir) {
        List<File> files = new ArrayList<>();
        File[] children = dir.listFiles();
        if (children != null) {
            for (File f : children) {
                if (f.isDirectory()) {
                    files.addAll(collectJavaFiles(f));
                } else if (f.getName().endsWith(".java")) {
                    files.add(f);
                }
            }
        }
        return files;
    }

    public void createJarFromCompiledClasses(File compiledDir, File jarFile, FileHandle sourceDir) throws IOException {
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile))) {
            // Add compiled classes
            addFilesToJar(compiledDir, compiledDir, jos);

            // Copy mod.json to JAR
            FileHandle modJson = sourceDir.child("mod.json");
            if (modJson.exists()) {
                jos.putNextEntry(new JarEntry("mod.json"));
                jos.write(modJson.readBytes());
                jos.closeEntry();
            }

            // Copy resources (assets, etc.) if they exist
            FileHandle assetsDir = sourceDir.child("assets");
            if (assetsDir.exists()) addDirectoryToJar(assetsDir.file(), compiledDir, jos, "assets/");
        }
    }

    public void addFilesToJar(File rootDir, File source, JarOutputStream target) throws IOException {
        if (source.isDirectory()) {
            String name = rootDir.toPath().relativize(source.toPath()).toString().replace("\\", "/");
            if (!name.isEmpty()) {
                if (!name.endsWith("/")) name += "/";
                JarEntry entry = new JarEntry(name);
                target.putNextEntry(entry);
                target.closeEntry();
            }
            for (File nestedFile : source.listFiles()) addFilesToJar(rootDir, nestedFile, target);
        } else {
            JarEntry entry = new JarEntry(rootDir.toPath().relativize(source.toPath()).toString().replace("\\", "/"));
            target.putNextEntry(entry);
            Files.copy(source.toPath(), target);
            target.closeEntry();
        }
    }

    public void addDirectoryToJar(File sourceDir, File rootDir, JarOutputStream jos, String pathPrefix) throws IOException {
        for (File file : sourceDir.listFiles()) {
            if (file.isDirectory()) {
                addDirectoryToJar(file, rootDir, jos, pathPrefix + file.getName() + "/");
            } else {
                jos.putNextEntry(new JarEntry(pathPrefix + file.getName()));
                Files.copy(file.toPath(), jos);
                jos.closeEntry();
            }
        }
    }

    public void deleteDirectory(File directory) {
        try {
            FileHandle dirHandle = Gdx.files.absolute(directory.getAbsolutePath());
            if (dirHandle.exists()) dirHandle.deleteDirectory();
        } catch (Exception e) {
            Main.logger.setTag("FunkyModLoader").warn("Failed to delete temp directory: " + directory.getPath(), e);
        }
    }

    public ModMetadata loadMetadataFromJar(FileHandle jarFile) {
        try (JarFile jar = new JarFile(jarFile.file())) {
            JarEntry entry = jar.getJarEntry("mod.json");
            if (entry == null) {
                Main.logger.setTag("FunkyModLoader").error("No mod.json found in " + jarFile.name());
                return null;
            }

            String jsonContent = new String(jar.getInputStream(entry).readAllBytes());
            return json.fromJson(ModMetadata.class, jsonContent);
        } catch (Exception e) {
            Main.logger.setTag("FunkyModLoader").error("Failed to read metadata from " + jarFile.name(), e);
            return null;
        }
    }

    private ModMetadata loadMetadataFromSource(FileHandle fileHandle) {
        FileHandle modJson = fileHandle.child("mod.json");
        if (modJson.exists()) {
            return loadMetadataFromJson(modJson);
        } else {
            Main.logger.setTag("FunkyModLoader").error("mod.json not found in " + fileHandle.path());
            return null;
        }
    }

    public ModMetadata loadMetadataFromJson(FileHandle jsonFile) {
        try {
            ModMetadata metadata = json.fromJson(ModMetadata.class, jsonFile);
            if (metadata != null && metadata.validate()) return metadata;
            return null;
        } catch (Exception e) {
            Main.logger.setTag("FunkyModLoader").error("Failed to parse " + jsonFile.path(), e);
            return null;
        }
    }

    public void handleDuplicateModId(ModMetadata newMod, FileHandle newFile, ModType newType, ModCandidate existing) {
        Main.logger.setTag("FunkyModLoader").error("Duplicate mod ID detected: '" + newMod.modId + "'");
        Main.logger.setTag("FunkyModLoader").error("  Existing: " + existing.file.path() + " (v" + existing.metadata.version + ")");
        Main.logger.setTag("FunkyModLoader").error("  Duplicate: " + newFile.path() + " (v" + newMod.version + ")");

        // Strategy 1: Always reject duplicates (safest)
        Main.logger.setTag("FunkyModLoader").error("  Skipping duplicate mod.");
    }

    @Override
    public void dispose() {
        for (LoadedModRecord mod : loadedMods) mod.dispose();
        loadedMods.clear();
        modRegistry.clear();
    }

    @Override
    public Array<LoadedMod> getLoadedMods() {
        Array<LoadedMod> result = new Array<>();
        for (LoadedModRecord record : loadedMods) result.add(record);
        return result;
    }

    public void updateMods() {
        loadedMods.forEach(mod -> mod.modules().forEach(module -> {
            try {
                module.update(Gdx.graphics.getDeltaTime());
            } catch (Exception e) {
                Main.logger.setTag("ModLoader").error("Update error in " + mod.metadata().modId, e);
            }
        }));
    }

    public void renderMods() {
        loadedMods.forEach(mod -> mod.modules().forEach(module -> {
            try {
                module.render(Main.instance.spriteBatch);
            } catch (Exception e) {
                Main.logger.setTag("ModLoader").error("Render error in " + mod.metadata().modId, e);
            }
        }));
    }

    public void pauseMods() {
        loadedMods.forEach(mod -> mod.modules().forEach(module -> {
            try {
                module.pause();
            } catch (Exception e) {
                Main.logger.setTag("ModLoader").error("Pause error in " + mod.metadata().modId, e);
            }
        }));
    }

    public void resumeMods() {
        loadedMods.forEach(mod -> mod.modules().forEach(module -> {
            try {
                module.render(Main.instance.spriteBatch);
            } catch (Exception e) {
                Main.logger.setTag("ModLoader").error("Resume error in " + mod.metadata().modId, e);
            }
        }));
    }

    public record ModCandidate(ModMetadata metadata, FileHandle file, ModType type) {}

    public enum ModType {
        JAR, SOURCE
    }

    public interface ModProgressCallback {
        void onProgress(String modName, float progress, String status);
    }
}
