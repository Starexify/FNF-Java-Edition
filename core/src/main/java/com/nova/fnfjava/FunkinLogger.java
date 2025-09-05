package com.nova.fnfjava;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FunkinLogger extends Logger {
    public static final String LOG_DIR = "logs/";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("[HH:mm:ss]");

    public FileHandle timestampedLogFile;
    public FileHandle latestLogFile;
    public FileHandle debugLogFile;
    public FileHandle crashLogFile;

    public boolean fileLoggingEnabled = true;
    public String customTag;

    public FunkinLogger(String tag, int level) {
        super(tag, level);
        this.customTag = tag;
        setupLogFile();
    }

    public void setupLogFile() {
        try {
            FileHandle logsDir = Gdx.files.local(LOG_DIR);
            if (!logsDir.exists()) logsDir.mkdirs();

            // Create timestamped log file
            String timestamp = DATE_FORMAT.format(new Date());

            // Create all log files
            timestampedLogFile = Gdx.files.local(LOG_DIR + timestamp + ".log");
            latestLogFile = Gdx.files.local(LOG_DIR + "latest.log");
            debugLogFile = Gdx.files.local(LOG_DIR + "debug.log");

            // Clear latest.log and debug.log, write headers
            latestLogFile.writeString("", false);
            debugLogFile.writeString("", false);

        } catch (Exception e) {
            System.err.println("Failed to setup log file: " + e.getMessage());
            fileLoggingEnabled = false;
        }
    }

    public void writeToFile(String message, boolean isDebug, boolean writeToLatest, boolean writeToDebug) {
        if (!fileLoggingEnabled) return;

        try {
            String timestamped = TIMESTAMP_FORMAT.format(new Date()) + " " + message;

            if (timestampedLogFile != null) {
                timestampedLogFile.writeString(timestamped + "\n", true);
            }

            if (writeToLatest && latestLogFile != null) {
                latestLogFile.writeString(timestamped + "\n", true);
            }

            if (writeToDebug && debugLogFile != null) {
                debugLogFile.writeString(timestamped + "\n", true);
            }
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

                // Add caused by if present
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
        // Log to all regular logs first
        error("FATAL CRASH: " + crashMessage, exception);

        // Write dedicated crash log
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

    public void writeExceptionToFiles(Throwable exception, boolean isDebug, boolean writeToLatest, boolean writeToDebug) {
        String exceptionMsg = "Exception: " + exception.toString();
        writeToFile(exceptionMsg, isDebug, writeToLatest, writeToDebug);

        for (StackTraceElement element : exception.getStackTrace()) writeToFile("    at " + element.toString(), isDebug, writeToLatest, writeToDebug);
    }

    public void performance(String operation, long timeMs) {
        String message = "Performance - " + operation + " took " + timeMs + "ms";
        writeToFile(formatLogLevel("PERF") + ": " + message, false, true, true);
        super.info(message);
    }

    public void shutdown() {

    }

    public String getCustomTag() {
        return customTag;
    }

    public FunkinLogger setTag(String newTag) {
        this.customTag = newTag;
        return this;
    }

    public boolean isFileLoggingEnabled() {
        return fileLoggingEnabled;
    }
}
