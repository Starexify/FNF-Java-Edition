package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

public class FunkinMixinLogger extends LoggerAdapterAbstract {
    public FunkinMixinLogger(String id) {
        super(id);
    }

    @Override
    public void catching(Level level, Throwable t) {
        this.log(level, "A throwable has been caught by the mixin subsystem: No further information.", t);
    }

    @Override
    public void log(Level level, String message, Object... params) {
        switch (level) {
            case DEBUG:
                this.debug(message, params);
                break;
            case ERROR:
                this.error(message, params);
                break;
            case FATAL:
                this.fatal(message, params);
                break;
            case INFO:
                this.info(message, params);
                break;
            case TRACE:
                this.trace(message, params);
                break;
            case WARN:
                this.warn(message, params);
                break;
            default:
                this.warn("Unknown logging level: {}", level);
                this.error(message, params);
                break;
        }
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        this.log(level, message, (Object) t);
    }

    @Override
    public void debug(String message, Object... params) {
        System.out.println("[DEBUG]" + message + "\n" + params);
    }

    @Override
    public void info(String message, Object... params) {
        System.out.println("[INFO]" + message + "\n" + params);
    }

    @Override
    public void trace(String message, Object... params) {
        System.out.println(message + "\n" + params);
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        return t;
    }

    @Override
    public String getType() {
        return "LoggerAdapterAbstract (extended by FunkinMixinLogger)";
    }
}
