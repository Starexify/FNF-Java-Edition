package com.nova.fnfjava;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Logger;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class FunkinLogger extends Logger {
    public static final String LOG_DIR = "logs/";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("[HH:mm:ss]");

    public FileHandle latestLogFile;
    public FileHandle debugLogFile;
    public FileHandle crashLogFile;

    public String customTag;
    public int maxDebugFiles = 5;
    public boolean fileLoggingEnabled = true;
    public boolean compressFiles = true; // Compress debug and timestamped files
    public long minFileSizeForCompression = 1024;

    public FunkinLogger(String tag, int level) {
        super(tag, level);
        this.customTag = tag;
        setupLogFile();
    }

    public void setupLogFile() {
        try {
            FileHandle logsDir = Gdx.files.local(LOG_DIR);
            if (!logsDir.exists()) logsDir.mkdirs();

            // Compress old files if enabled
            if (compressFiles) {
                compressOldFiles();
            }

            // Create current session files
            latestLogFile = Gdx.files.local(LOG_DIR + "latest.log");
            debugLogFile = Gdx.files.local(LOG_DIR + "debug.log");

            // Archive existing debug files
            archiveDebugFiles();

            // Clear current session files
            latestLogFile.writeString("", false);
            debugLogFile.writeString("", false);

        } catch (Exception e) {
            System.err.println("Failed to setup log file: " + e.getMessage());
            fileLoggingEnabled = false;
        }
    }

    public void archiveDebugFiles() {
        try {
            FileHandle currentDebug = Gdx.files.local(LOG_DIR + "debug.log");
            if (!currentDebug.exists() || currentDebug.length() == 0) return;

            // Shift existing archived files backwards
            for (int i = maxDebugFiles - 1; i >= 1; i--) {
                FileHandle sourceGz = Gdx.files.local(LOG_DIR + "debug-" + i + ".log.gz");
                FileHandle sourceLog = Gdx.files.local(LOG_DIR + "debug-" + i + ".log");

                FileHandle source = sourceGz.exists() ? sourceGz : (sourceLog.exists() ? sourceLog : null);

                if (source != null) {
                    if (i + 1 <= maxDebugFiles) {
                        FileHandle target = Gdx.files.local(LOG_DIR + "debug-" + (i + 1) + ".log.gz");

                        if (source.extension().equals("gz")) source.moveTo(target);
                        else compressAndMove(source, target);

                    } else {
                        source.delete();
                    }
                }
            }

            // Move current debug.log to debug-1.log.gz (always compress archived files)
            FileHandle debugArchive1 = Gdx.files.local(LOG_DIR + "debug-1.log.gz");
            compressAndMove(currentDebug, debugArchive1);

        } catch (Exception e) {
            System.err.println("Failed to archive debug files: " + e.getMessage());
        }
    }

    public void compressOldFiles() {
        try {
            FileHandle logsDir = Gdx.files.local(LOG_DIR);
            if (!logsDir.exists()) return;

            FileHandle[] logFiles = logsDir.list(".log");

            for (FileHandle logFile : logFiles) {
                String name = logFile.nameWithoutExtension();

                // Skip current session files and crash files
                if (name.equals("latest") || name.equals("debug") || name.startsWith("crash-")) {
                    continue;
                }

                // Compress old timestamped files and debug archives
                if ((isTimestampedFile(name) || name.startsWith("debug-")) && shouldCompress(logFile)) {
                    FileHandle compressedFile = Gdx.files.local(logFile.path() + ".gz");

                    if (!compressedFile.exists()) {
                        compressFile(logFile, compressedFile);
                        logFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to compress old files: " + e.getMessage());
        }
    }

    public void writeToFile(String message, boolean isDebug, boolean writeToLatest, boolean writeToDebug) {
        if (!fileLoggingEnabled) return;

        try {
            String timestamped = TIMESTAMP_FORMAT.format(new Date()) + " " + message;
            if (writeToLatest && latestLogFile != null) latestLogFile.writeString(timestamped + "\n", true);
            if (writeToDebug && debugLogFile != null) debugLogFile.writeString(timestamped + "\n", true);

        } catch (Exception e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
            fileLoggingEnabled = false;
        }
    }

    public void writeCrashLog(String crashMessage, Throwable exception) {
        try {
            if (crashLogFile == null) {
                String timestamp = DATE_FORMAT.format(new Date());
                crashLogFile = Gdx.files.local(LOG_DIR + "crash-" + timestamp + ".log");
            }

            StringBuilder crashReport = new StringBuilder();
            crashReport.append("---- Friday Night Funkin Crash Report ----\n");
            crashReport.append("// Sorry! :(\n\n");
            crashReport.append("Time: ").append(new Date()).append("\n");
            crashReport.append("Description: ").append(crashMessage).append("\n\n");

            if (exception != null) {
                crashReport.append("Exception: ").append(exception).append("\n");
                for (StackTraceElement element : exception.getStackTrace()) {
                    crashReport.append("\tat ").append(element.toString()).append("\n");
                }

                Throwable cause = exception.getCause();
                while (cause != null) {
                    crashReport.append("Caused by: ").append(cause).append("\n");
                    for (StackTraceElement element : cause.getStackTrace()) {
                        crashReport.append("\tat ").append(element.toString()).append("\n");
                    }
                    cause = cause.getCause();
                }
            }

            crashLogFile.writeString(crashReport.toString(), false);

        } catch (Exception e) {
            System.err.println("Failed to write crash log: " + e.getMessage());
        }
    }

    public void writeExceptionToFiles(Throwable exception, boolean isDebug, boolean writeToLatest, boolean writeToDebug) {
        String exceptionMsg = "Exception: " + exception.toString();
        writeToFile(exceptionMsg, isDebug, writeToLatest, writeToDebug);

        for (StackTraceElement element : exception.getStackTrace()) writeToFile("    at " + element.toString(), isDebug, writeToLatest, writeToDebug);
    }

    public String formatLogLevel(String level) {
        return "[" + customTag + "/" + level + "]";
    }

    @Override
    public void info(String message) {
        super.info(message);
        writeToFile(formatLogLevel("INFO") + ": " + message, false, true, true);
    }

    @Override
    public void info(String message, Exception exception) {
        super.info(message, exception);
        writeToFile(formatLogLevel("INFO") + ": " + message, false, true, true);
        if (exception != null) writeExceptionToFiles(exception, false, true, true);
    }

    public void warn(String message) {
        super.error(message);
        writeToFile(formatLogLevel("WARN") + ": " + message, false, true, true);
    }

    public void warn(String message, Exception exception) {
        super.error(message, exception);
        writeToFile(formatLogLevel("WARN") + ": " + message, false, true, true);
        if (exception != null) writeExceptionToFiles(exception, false, true, true);
    }

    @Override
    public void error(String message) {
        super.error(message);
        writeToFile(formatLogLevel("ERROR") + ": " + message, false, true, true);
    }

    @Override
    public void error(String message, Throwable exception) {
        super.error(message, exception);
        writeToFile(formatLogLevel("ERROR") + ": " + message, false, true, true);
        if (exception != null) writeExceptionToFiles(exception, false, true, true);
    }

    @Override
    public void debug(String message) {
        super.debug(message);
        writeToFile(formatLogLevel("DEBUG") + ": " + message, true, false, true);
    }

    @Override
    public void debug(String message, Exception exception) {
        super.debug(message, exception);
        writeToFile(formatLogLevel("DEBUG") + ": " + message, true, false, true);
        if (exception != null) writeExceptionToFiles(exception, true, false, true);
    }

    // Crash-specific methods
    public void crash(String crashMessage) {
        crash(crashMessage, null);
    }

    public void crash(String crashMessage, Throwable exception) {
        saveTimestampedSnapshot();

        archiveDebugFiles();

        error("FATAL CRASH: " + crashMessage, exception);
        writeCrashLog(crashMessage, exception);

        System.err.println("Game crashed! Check crash log for details.");
    }

    // Additional logging methods with custom tags (one-time use)
    public void info(String tag, String message) {
        super.info(message);
        writeToFile("[" + tag + "/INFO]: " + message, false, true, true);
    }

    public void error(String tag, String message) {
        super.error(message);
        writeToFile("[" + tag + "/ERROR]: " + message, false, true, true);
    }

    public void debug(String tag, String message) {
        super.debug(message);
        writeToFile("[" + tag + "/DEBUG]: " + message, true, false, true);
    }

    public void error(String tag, String message, Throwable exception) {
        super.error(message, exception);
        writeToFile("[" + tag + "/ERROR]: " + message, false, true, true);
        if (exception != null) writeExceptionToFiles(exception, false, true, true);
    }

    public void performance(String operation, long timeMs) {
        String message = "Performance - " + operation + " took " + timeMs + "ms";
        writeToFile(formatLogLevel("PERF") + ": " + message, false, true, true);
        super.info(message);
    }

    public void shutdown() {
        try {
            System.out.println("Logger shutting down...");

            // Save current session as timestamped file
            saveTimestampedSnapshot();

            // Archive current debug file
            archiveDebugFiles();

            // Clean up old files
            cleanupOldFiles();

            System.out.println("Logger shutdown complete.");

        } catch (Exception e) {
            System.err.println("Error during logger shutdown: " + e.getMessage());
        }
    }

    public void saveTimestampedSnapshot() {
        try {
            String timestamp = DATE_FORMAT.format(new Date());

            // Save latest.log as timestamped file
            if (latestLogFile != null && latestLogFile.exists() && latestLogFile.length() > 0) {
                FileHandle timestampedFile = Gdx.files.local(LOG_DIR + timestamp + ".log");
                latestLogFile.copyTo(timestampedFile);

                // Compress if enabled and file is large enough
                if (compressFiles && shouldCompress(timestampedFile)) {
                    FileHandle compressedFile = Gdx.files.local(timestampedFile.path() + ".gz");
                    compressFile(timestampedFile, compressedFile);
                    timestampedFile.delete();
                    System.out.println("Saved and compressed session as: " + compressedFile.name());
                } else {
                    System.out.println("Saved session as: " + timestampedFile.name());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to save timestamped snapshot: " + e.getMessage());
        }
    }

    public void cleanupOldFiles() {
        try {
            // Clean up old debug files beyond max
            for (int i = maxDebugFiles + 1; i <= maxDebugFiles + 10; i++) {
                FileHandle oldGz = Gdx.files.local(LOG_DIR + "debug-" + i + ".log.gz");
                FileHandle oldLog = Gdx.files.local(LOG_DIR + "debug-" + i + ".log");

                if (oldGz.exists()) {
                    oldGz.delete();
                    System.out.println("Deleted old debug file: debug-" + i + ".log.gz");
                }
                if (oldLog.exists()) {
                    oldLog.delete();
                    System.out.println("Deleted old debug file: debug-" + i + ".log");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to cleanup old files: " + e.getMessage());
        }
    }

    public FunkinLogger setTag(String newTag) {
        this.customTag = newTag;
        return this;
    }

    public boolean isTimestampedFile(String filename) {
        return filename.matches("\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}");
    }

    // Compression helpers
    public void compressAndMove(FileHandle source, FileHandle target) {
        if (compressFiles && shouldCompress(source)) {
            compressFile(source, target);
            source.delete();
        } else {
            // If not compressing, just move to .log extension
            FileHandle uncompressedTarget = Gdx.files.local(target.path().replace(".gz", ""));
            source.moveTo(uncompressedTarget);
        }
    }

    public void compressFile(FileHandle source, FileHandle target) {
        try {
            String content = source.readString();
            byte[] compressed = compressString(content);
            target.writeBytes(compressed, false);

            long originalSize = source.length();
            long compressedSize = target.length();
            double ratio = (1.0 - (double) compressedSize / originalSize) * 100;

            System.out.println("Compressed " + source.name() + " -> " + target.name() +
                " (saved " + String.format("%.1f", ratio) + "%)");

        } catch (Exception e) {
            System.err.println("Failed to compress " + source.name() + ": " + e.getMessage());
            // Fallback: copy uncompressed
            try {
                source.copyTo(Gdx.files.local(target.path().replace(".gz", "")));
            } catch (Exception ex) {
                System.err.println("Fallback copy failed: " + ex.getMessage());
            }
        }
    }

    public byte[] compressString(String data) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(baos)) {
            gzipOut.write(data.getBytes("UTF-8"));
        }
        return baos.toByteArray();
    }

    public boolean shouldCompress(FileHandle file) {
        return file.exists() && file.length() >= minFileSizeForCompression;
    }
}
