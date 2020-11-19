package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;

public abstract class ElementEvent<T extends Element> extends Event<T> {

  protected ElementEvent(T target) {
    super(target);
  }

  protected ElementEvent(T target, double timeStamp) {
    super(target, timeStamp);
  }

  protected ElementEvent(EventTarget source, T target, double timeStamp) {
    super(source, target, timeStamp);
  }
}
