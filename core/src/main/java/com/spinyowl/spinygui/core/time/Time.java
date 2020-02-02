package com.spinyowl.spinygui.core.time;

import java.time.Instant;
import java.util.Objects;

/**
 * Singleton Time class. Holds TimeProvider instance that used to obtain time.
 */
public final class Time {
    public static final TimeProvider DEFAULT_TIME_PROVIDER = () -> {
        Instant now = Instant.now();
        return now.getEpochSecond() + now.getNano() / 1_000_000_000d;
    };
    private static TimeProvider instance = DEFAULT_TIME_PROVIDER;

    private Time() {
    }

    /**
     * Used to get current time from time provider delegate.
     *
     * Returns current time in seconds since epoch of 1970-01-01T00:00:00Z.
     *
     * @return current time in seconds since epoch of 1970-01-01T00:00:00Z.
     */
    public static double getCurrentTime() {
        return instance.getCurrentTime();
    }

    /**
     * Used to change time provider.
     * @param instance new time provider.
     * @throws NullPointerException in case if provided instance is null.
     */
    public static void setTimeProvider(TimeProvider instance) {
        Objects.requireNonNull(instance);
        Time.instance = instance;
    }
}
