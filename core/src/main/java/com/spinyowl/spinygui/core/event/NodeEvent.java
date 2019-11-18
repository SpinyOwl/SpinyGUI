package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.base.Element;

public abstract class NodeEvent<T extends Element> extends Event<T> {
    public NodeEvent(T target) {
        super(target);
    }

    public NodeEvent(T target, double timeStamp) {
        super(target, timeStamp);
    }

    public NodeEvent(EventTarget source, T target, double timeStamp) {
        super(source, target, timeStamp);
    }
}
