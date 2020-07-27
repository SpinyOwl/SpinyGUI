package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.node.Element;

public abstract class ElementEvent<T extends Element> extends Event<T> {

  public ElementEvent(T target) {
    super(target);
  }

  public ElementEvent(T target, double timeStamp) {
    super(target, timeStamp);
  }

  public ElementEvent(EventTarget source, T target, double timeStamp) {
    super(source, target, timeStamp);
  }
}
