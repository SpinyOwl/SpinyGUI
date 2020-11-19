package com.spinyowl.spinygui.core.event;

import com.spinyowl.spinygui.core.time.Time;
import lombok.Getter;

@Getter
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

  protected Event(T target) {
    this(target, Time.getCurrentTime());
  }

  protected Event(T target, double timeStamp) {
    this(null, target, timeStamp);
  }

  protected Event(EventTarget source, T target, double timeStamp) {
    this.source = source;
    this.target = target;
    this.timeStamp = timeStamp;
  }

}
