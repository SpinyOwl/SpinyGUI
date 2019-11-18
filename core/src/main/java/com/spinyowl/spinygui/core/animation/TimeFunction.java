package com.spinyowl.spinygui.core.animation;

/**
 * Time function interface. Used to get current time in double representation.
 */
public interface TimeFunction {

    /**
     * Returns current time in seconds since epoch of 1970-01-01T00:00:00Z.
     *
     * @return current time in seconds since epoch of 1970-01-01T00:00:00Z.
     */
    double getCurrentTime();

}
