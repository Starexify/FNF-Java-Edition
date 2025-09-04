package com.nova.fnfjava.api.discord;

import com.badlogic.gdx.Gdx;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordClient {
    public static final long CLIENT_ID = 1412755903073091625L;

    public static DiscordClient instance;
    public static Core core;
    public static boolean initialized = false;
    public static ScheduledExecutorService callbackExecutor;

    public static DiscordClient getInstance() {
        if (instance == null) instance = new DiscordClient();
        return instance;
    }

    public static void init() {
        if (initialized) return;

        Gdx.app.log("DISCORD", "Initializing connection...");

        try (CreateParams params = new CreateParams()) {
            params.setClientID(CLIENT_ID);
            params.setFlags(CreateParams.getDefaultFlags());
            try {
                core = new Core(params);
                initialized = true;

                createCallbackDaemon();

                setPresence(new DiscordPresenceParams(null, "Just Started Playing"));
            } catch (Exception e) {
                Gdx.app.error("DISCORD", "" + e);
                initialized = false;
            }
        }
    }

    public static void shutdown() {
        Gdx.app.log("DISCORD", "Shutting down...");
        try {
            if (callbackExecutor != null) callbackExecutor.shutdown();
            if (core != null) core.close();
        } catch (Exception e) {
            Gdx.app.error("DISCORD", "Error during shutdown: " + e.getMessage());
        }
        initialized = false;
    }

    public static void setPresence(DiscordPresenceParams params) {
        if (!initialized || core == null) return;

        try (Activity activity = buildActivity(params)) {
            core.activityManager().updateActivity(activity);
        } catch (Exception e) {
            Gdx.app.error("DISCORD", "Failed to update presence: " + e.getMessage());
        }
    }

    public static Activity buildActivity(DiscordPresenceParams params) {
        Activity activity = new Activity();

        activity.timestamps().setStart(Instant.now());

        activity.assets().setLargeText("Friday Night Funkin': Java Edition");

        if (params.state != null && !params.state.isEmpty()) activity.setState(params.state);
        if (params.details != null && !params.details.isEmpty()) activity.setDetails(params.details);
        if (params.largeImageKey != null && !params.largeImageKey.isEmpty())
            activity.assets().setLargeImage(params.largeImageKey);
        else activity.assets().setLargeImage("icon");
        if (params.smallImageKey != null && !params.smallImageKey.isEmpty())
            activity.assets().setSmallImage(params.smallImageKey);

        return activity;
    }

    public static void createCallbackDaemon() {
        callbackExecutor = Executors.newSingleThreadScheduledExecutor();
        callbackExecutor.scheduleAtFixedRate(() -> {
            if (initialized && core != null) {
                try {
                    core.runCallbacks();
                } catch (Exception e) {
                    Gdx.app.error("DISCORD", "Callback error: " + e.getMessage());
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public static class DiscordPresenceParams {
        public String state;
        public String details;
        public String largeImageKey;
        public String smallImageKey;

        public DiscordPresenceParams(String state, String details, String largeImageKey, String smallImageKey) {
            this.state = state;
            this.details = details;
            this.largeImageKey = largeImageKey;
            this.smallImageKey = smallImageKey;
        }

        public DiscordPresenceParams(String state, String details) {
            this(state, details, null, null);
        }
    }
}
