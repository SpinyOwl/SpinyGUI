package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.event.KeyboardEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.MultilineTextControlMetrics;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.system.input.TextInputBehavior;
import com.spinyowl.spinygui.core.system.input.TextInputViewportBehavior;
import com.spinyowl.spinygui.core.system.input.TextareaBehavior;
import com.spinyowl.spinygui.core.system.input.TextareaViewportBehavior;
import com.spinyowl.spinygui.core.time.TimeService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode
public class SystemKeyEventListener extends AbstractSystemEventListener<SystemKeyEvent> {

  private static final TextInputBehavior TEXT_INPUT_BEHAVIOR = new TextInputBehavior();
  @EqualsAndHashCode.Exclude private final TextareaBehavior textareaBehavior;

  @NonNull private final Keyboard keyboard;
  @EqualsAndHashCode.Exclude private final TextInputViewportBehavior viewportBehavior;
  @EqualsAndHashCode.Exclude private final TextareaViewportBehavior textareaViewportBehavior;

  @Builder
  public SystemKeyEventListener(
      @NonNull EventProcessor eventProcessor,
      @NonNull TimeService timeService,
      @NonNull Keyboard keyboard,
      TextMeasurer textMeasurer) {
    super(eventProcessor, timeService);
    this.keyboard = keyboard;
    viewportBehavior = textMeasurer == null ? null : new TextInputViewportBehavior(textMeasurer);
    MultilineTextControlMetrics textareaMetrics =
        textMeasurer == null ? null : new MultilineTextControlMetrics(textMeasurer);
    textareaBehavior = new TextareaBehavior(textareaMetrics);
    textareaViewportBehavior =
        textareaMetrics == null ? null : new TextareaViewportBehavior(textareaMetrics);
  }

  /**
   * Used to listen, process and translate system event to gui event.
   *
   * @param event system event to process
   * @param frame target frame for system event.
   */
  @Override
  public void process(@NonNull SystemKeyEvent event, @NonNull Frame frame) {
    var element = frame.getFocusedElement();
    if (element != null) {

      int keyCode = event.keyCode();
      var keyCodeObject = keyboard.layout().keyCode(keyCode);
      if (keyCodeObject == null) {
        keyCodeObject = KeyCode.UNKNOWN;
      }
      var key = new KeyboardKey(keyCodeObject, keyCode, event.scancode());
      var action = getAction(event);

      if (element instanceof InputElement input) {
        boolean changed =
            TEXT_INPUT_BEHAVIOR.handleKey(
                input, keyCodeObject, action, event.mods().contains(SystemKeyMod.SHIFT));
        if (changed) {
          ensureCaretVisible(input);
        }
      } else if (element instanceof TextareaElement textarea) {
        boolean changed =
            textareaBehavior.handleKey(
                textarea, keyCodeObject, action, event.mods().contains(SystemKeyMod.SHIFT));
        if (changed) {
          ensureCaretVisible(textarea);
        }
      }

      eventProcessor.push(
          KeyboardEvent.builder()
              .source(frame)
              .target(element)
              .key(key)
              .timestamp(timeService.currentTime())
              .mods(event.mappedMods())
              .action(action)
              .build());
    }
  }

  private KeyAction getAction(SystemKeyEvent event) {
    return switch (event.action()) {
      case PRESS -> KeyAction.PRESS;
      case RELEASE -> KeyAction.RELEASE;
      case REPEAT -> KeyAction.REPEAT;
    };
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
