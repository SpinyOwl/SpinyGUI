package com.spinyowl.spinygui.core.event.listener;

import com.spinyowl.spinygui.core.event.Event;

public interface Listener<T extends Event> {

    void process(T event);

}
