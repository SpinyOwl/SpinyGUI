package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.CharEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.event.processor.InputProcessingBatch;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.MultilineTextControlMetrics;
import com.spinyowl.spinygui.core.system.input.TextInputBehavior;
import com.spinyowl.spinygui.core.system.input.TextInputViewportBehavior;
import com.spinyowl.spinygui.core.system.input.TextareaBehavior;
import com.spinyowl.spinygui.core.system.input.TextareaViewportBehavior;
import com.spinyowl.spinygui.core.time.TimeService;
import com.spinyowl.spinygui.core.util.TextUtil;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class SystemCharEventListener extends AbstractSystemEventListener<SystemCharEvent> {

  private static final TextInputBehavior TEXT_INPUT_BEHAVIOR = new TextInputBehavior();
  private static final TextareaBehavior TEXTAREA_BEHAVIOR = new TextareaBehavior();
  @EqualsAndHashCode.Exclude private final TextInputViewportBehavior viewportBehavior;
  @EqualsAndHashCode.Exclude private final TextareaViewportBehavior textareaViewportBehavior;

  @Builder
  public SystemCharEventListener(
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      TextMeasurer textMeasurer) {
    super(eventProcessor, timeService);
    viewportBehavior = textMeasurer == null ? null : new TextInputViewportBehavior(textMeasurer);
    textareaViewportBehavior =
        textMeasurer == null
            ? null
            : new TextareaViewportBehavior(new MultilineTextControlMetrics(textMeasurer));
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemCharEvent event, @NonNull Frame frame) {
    processInternal(event, frame, null);
  }

  @Override
  public void processWithImpact(
      @NonNull SystemCharEvent event,
      @NonNull Frame frame,
      @NonNull InputProcessingBatch batch) {
    processInternal(event, frame, batch);
  }

  private void processInternal(
      SystemCharEvent event, Frame frame, InputProcessingBatch batch) {
    var focusedElement = frame.getFocusedElement();
    if (focusedElement == null || focusedElement.disabled()) {
      return;
    }

    boolean changed = false;
    if (focusedElement instanceof InputElement input) {
      changed = TEXT_INPUT_BEHAVIOR.insertPrintable(input, event.codepoint());
      if (changed) {
        ensureCaretVisible(input);
      }
    } else if (focusedElement instanceof TextareaElement textarea) {
      changed = TEXTAREA_BEHAVIOR.insertPrintable(textarea, event.codepoint());
      if (changed) {
        ensureCaretVisible(textarea);
      }
    }
    if (changed) {
      batchMarkKnownEffect(batch);
    }
    if (focusedElement.hasListenersFor(CharEvent.class)) {
      batchMarkUnknownFallback(batch);
    }

    eventProcessor.push(
        CharEvent.builder()
            .source(frame)
            .target(focusedElement)
            .timestamp(timeService.currentTime())
            .input(TextUtil.cpToStr(event.codepoint()))
            .build());
  }

  private void batchMarkKnownEffect(InputProcessingBatch batch) {
    if (batch != null) {
      batch.markKnownEffect();
    }
  }

  private void batchMarkUnknownFallback(InputProcessingBatch batch) {
    if (batch != null) {
      batch.markUnknownFallback();
    }
  }

  private void ensureCaretVisible(InputElement input) {
    if (viewportBehavior != null) {
      viewportBehavior.ensureCaretVisible(input);
    }
  }

  private void ensureCaretVisible(TextareaElement textarea) {
    if (textareaViewportBehavior != null) {
      textareaViewportBehavior.ensureCaretVisible(textarea);
    }
  }
}
