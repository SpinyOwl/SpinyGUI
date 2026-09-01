package com.spinyowl.spinygui.core.system.event.listener;

import com.spinyowl.spinygui.core.clipboard.Clipboard;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardLayout;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.impl.ShortcutRegistryImpl;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorEnterEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowSizeEvent;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutService;
import com.spinyowl.spinygui.core.system.input.ScrollbarInteraction;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.Objects;

/** Creates the standard listener provider used by the reusable application compositions. */
public final class DefaultSystemEventListeners {

  private DefaultSystemEventListeners() {}

  /**
   * Creates one listener for every system event emitted by the standard window integration.
   *
   * <p>All listeners share the supplied GUI event, time, and mouse services. Text-aware listeners
   * additionally share one control-text-layout service, and pointer listeners share one scrollbar
   * interaction so capture state remains coherent across cursor and button callbacks.
   *
   * @param guiEvents destination for translated GUI events
   * @param timeService timestamp source shared by listeners
   * @param mouseService pointer state shared by listeners
   * @param clipboard clipboard used by keyboard shortcuts
   * @param keyboardLayout native-to-semantic keyboard mapping
   * @param textMeasurer text measurement service shared by control behavior
   * @return provider containing the standard listener set
   */
  public static SystemEventListenerProviderImpl create(
      EventProcessor guiEvents,
      TimeService timeService,
      MouseService mouseService,
      Clipboard clipboard,
      KeyboardLayout keyboardLayout,
      TextMeasurer textMeasurer) {
    Objects.requireNonNull(guiEvents, "guiEvents");
    Objects.requireNonNull(timeService, "timeService");
    Objects.requireNonNull(mouseService, "mouseService");
    Objects.requireNonNull(clipboard, "clipboard");
    Objects.requireNonNull(keyboardLayout, "keyboardLayout");
    Objects.requireNonNull(textMeasurer, "textMeasurer");

    SystemEventListenerProviderImpl listeners = new SystemEventListenerProviderImpl();
    ScrollbarInteraction scrollbarInteraction = new ScrollbarInteraction();
    ControlTextLayoutService controlTextLayoutService =
        new ControlTextLayoutService(textMeasurer);
    Keyboard keyboard = new Keyboard(keyboardLayout, new ShortcutRegistryImpl());

    listeners.listener(
        SystemCursorPosEvent.class,
        SystemCursorPosEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .mouseService(mouseService)
            .scrollbarInteraction(scrollbarInteraction)
            .controlTextLayoutService(controlTextLayoutService)
            .build());
    listeners.listener(
        SystemCursorEnterEvent.class,
        SystemCursorEnterEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .mouseService(mouseService)
            .build());
    listeners.listener(
        SystemWindowSizeEvent.class,
        SystemWindowSizeEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .build());
    listeners.listener(
        SystemScrollEvent.class,
        SystemScrollEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .mouseService(mouseService)
            .controlTextLayoutService(controlTextLayoutService)
            .build());
    listeners.listener(
        SystemMouseClickEvent.class,
        SystemMouseClickEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .mouseService(mouseService)
            .scrollbarInteraction(scrollbarInteraction)
            .controlTextLayoutService(controlTextLayoutService)
            .build());
    listeners.listener(
        SystemCharEvent.class,
        SystemCharEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .controlTextLayoutService(controlTextLayoutService)
            .build());
    listeners.listener(
        SystemKeyEvent.class,
        SystemKeyEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(timeService)
            .keyboard(keyboard)
            .clipboard(clipboard)
            .controlTextLayoutService(controlTextLayoutService)
            .build());
    return listeners;
  }
}
