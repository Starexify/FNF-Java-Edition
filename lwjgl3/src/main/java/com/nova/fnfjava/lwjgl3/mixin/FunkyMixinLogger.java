package com.nova.fnfjava.lwjgl3.mixin;

import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

public class FunkyMixinLogger extends LoggerAdapterAbstract {
    public FunkyMixinLogger(String id) {
        super(id);
    }

    @Override
    public void catching(Level level, Throwable t) {
        this.log(level, "A throwable has been caught by the mixin subsystem: No further information.", t);
    }

    @Override
    public void log(Level level, String message, Object... params) {
        FormattedMessage formatted = new FormattedMessage(message, params);
        String levelPrefix = "[" + level.name() + "] ";

        System.out.println(levelPrefix + formatted.getMessage());

        if (formatted.hasThrowable()) formatted.getThrowable().printStackTrace(System.out);
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        String levelPrefix = "[" + level.name() + "] ";
        System.out.println(levelPrefix + message);
        if (t != null) t.printStackTrace(System.out);
    }

    @Override
    public void debug(String message, Object... params) {
        FormattedMessage formatted = new FormattedMessage(message, params);
        System.out.println("[DEBUG] " + formatted.getMessage());
        if (formatted.hasThrowable()) formatted.getThrowable().printStackTrace(System.out);
    }

    @Override
    public void info(String message, Object... params) {
        FormattedMessage formatted = new FormattedMessage(message, params);
        System.out.println("[INFO] " + formatted.getMessage());
        if (formatted.hasThrowable()) formatted.getThrowable().printStackTrace(System.out);
    }

    @Override
    public void trace(String message, Object... params) {
        FormattedMessage formatted = new FormattedMessage(message, params);
        System.out.println("[TRACE] " + formatted.getMessage());
        if (formatted.hasThrowable()) formatted.getThrowable().printStackTrace(System.out);
    }

    @Override
    public void warn(String message, Object... params) {
        FormattedMessage formatted = new FormattedMessage(message, params);
        System.out.println("[WARN] " + formatted.getMessage());
        if (formatted.hasThrowable()) formatted.getThrowable().printStackTrace(System.out);
    }

    @Override
    public void error(String message, Object... params) {
        FormattedMessage formatted = new FormattedMessage(message, params);
        System.out.println("[ERROR] " + formatted.getMessage());
        if (formatted.hasThrowable()) formatted.getThrowable().printStackTrace(System.out);
    }

    @Override
    public void fatal(String message, Object... params) {
        FormattedMessage formatted = new FormattedMessage(message, params);
        System.out.println("[FATAL] " + formatted.getMessage());
        if (formatted.hasThrowable()) formatted.getThrowable().printStackTrace(System.out);
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
