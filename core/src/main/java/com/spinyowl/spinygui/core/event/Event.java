package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.time.Time;

public abstract class Event<T extends EventTarget> {

    /**
     * Element which cause event generation.
     */
    private final EventTarget source;
    /**
     * Target element which event listeners should be processed.
     */
    private final T target;
    /**
     * TimeStamp of event.
     */
    private final double timeStamp;

    public Event(T target) {
        this(target, Time.getCurrentTime());
    }

    public Event(T target, double timeStamp) {
        this(null, target, timeStamp);
    }

    public Event(EventTarget source, T target, double timeStamp) {
        this.source = source;
        this.target = target;
        this.timeStamp = timeStamp;
    }

    public EventTarget getSource() {
        return source;
    }

    public T getTarget() {
        return target;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

}
