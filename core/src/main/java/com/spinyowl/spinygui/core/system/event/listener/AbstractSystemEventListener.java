package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.api.Frame;
import com.spinyowl.spinygui.core.api.Layer;
import com.spinyowl.spinygui.core.system.event.SystemEvent;

public abstract class AbstractSystemEventListener<E extends SystemEvent>
    implements SystemEventListener<E> {

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(E event, Frame frame) {
    preProcess(event, frame);

    for (Layer layer : frame.reversedLayers()) {

      if (layer.eventReceivable()) {

        if (layer.visible()) {
          if (process(event, frame, layer)) {
            return;
          }
        }

      }

      if (!layer.eventPassable()) {
        break;
      }
    }

    postProcess(event, frame);

  }

  protected void preProcess(E event, Frame frame) {
    // this method could be overridden to pre-process some event
  }
  
  protected abstract boolean process(E event, Frame frame, Layer layer);

  protected void postProcess(E event, Frame frame) {
    // this method could be overridden to post-process some event
  }
}
