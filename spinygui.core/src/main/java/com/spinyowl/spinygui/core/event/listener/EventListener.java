package com.spinyowl.spinygui.core.event.listener;

import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.processor.InputProcessingBatch;
import java.util.Objects;

public interface EventListener<T extends Event> {

  void process(T event);

  /**
   * Dispatch adapter for conservative input-impact reporting.
   *
   * <p>Existing listeners remain source-compatible and are treated as unknown. A listener must
   * explicitly override this method before its dispatch can contribute a proven unchanged result.
   */
  default void processWithImpact(T event, InputProcessingBatch batch) {
    Objects.requireNonNull(batch, "batch");
    process(event);
    batch.markUnknownFallback();
  }
}
