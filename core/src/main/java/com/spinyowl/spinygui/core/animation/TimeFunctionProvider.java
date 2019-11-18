package com.spinyowl.spinygui.core.animation;

import java.time.Instant;
import java.util.Objects;

public final class TimeFunctionProvider {
    public static final TimeFunction DEFAULT_TIME_FUNCTION = () -> {
        Instant now = Instant.now();
        return now.getEpochSecond() + now.getNano() / 1_000_000_000d;
    };
    private static TimeFunction instance = DEFAULT_TIME_FUNCTION;

    private TimeFunctionProvider() {
    }

    public static TimeFunction getInstance() {
        return instance;
    }

    public static void setInstance(TimeFunction instance) {
        Objects.requireNonNull(instance);
        TimeFunctionProvider.instance = instance;
    }
}
