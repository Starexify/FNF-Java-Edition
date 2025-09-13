package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FunkyMixinLogger extends LoggerAdapterAbstract {
    public static final String LOG_DIR = "logs";
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("[HH:mm:ss]");
    public final Path logFile;

    public FunkyMixinLogger(String id) throws IOException {
        super(id);
        Path logDir = Paths.get(LOG_DIR);
        if (!Files.exists(logDir)) Files.createDirectories(logDir);

        logFile = logDir.resolve("mixin.log");

        Files.writeString(logFile, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public String timestamp() {
        return TIMESTAMP_FORMAT.format(new Date());
    }

    public void writeToFile(String message) {
        String line = timestamp() + " " + message;
        System.out.println(line);
        try {
            Files.writeString(logFile, line + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeThrowable(Throwable t, Path file) {
        if (t == null) return;
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : t.getStackTrace()) sb.append("\tat ").append(e).append("\n");
        System.out.print(sb);
        if (file != null) {
            try {
                Files.writeString(file, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String formatLine(Level level) {
        return "[Mixin/" + level.name() + "] ";
    }

    @Override
    public void catching(Level level, Throwable t) {
        this.log(level, "A throwable has been caught by the mixin subsystem: No further information.", t);
    }

    @Override
    public void log(Level level, String message, Object... params) {
        FormattedMessage formatted = new FormattedMessage(message, params);
        String logLine = formatLine(level) + formatted.getMessage();
        writeToFile(logLine);
        if (formatted.hasThrowable()) writeThrowable(formatted.getThrowable(), logFile);
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        String logLine = formatLine(level) + message;
        writeToFile(logLine);
        writeThrowable(t, logFile);
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        return t;
    }

    @Override
    public String getType() {
        return "FunkyMixinLogger";
    }
}
