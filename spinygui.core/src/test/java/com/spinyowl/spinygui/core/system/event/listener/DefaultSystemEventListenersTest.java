package com.spinyowl.spinygui.core.system.event.listener;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import com.spinyowl.spinygui.core.clipboard.Clipboard;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.input.KeyboardLayout;
import com.spinyowl.spinygui.core.input.MouseService;
import com.spinyowl.spinygui.core.input.impl.KeyboardLayoutImpl;
import com.spinyowl.spinygui.core.input.impl.MouseServiceImpl;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemCharModsEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorEnterEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemFileDropEvent;
import com.spinyowl.spinygui.core.system.event.SystemFramebufferSizeEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.event.SystemMouseClickEvent;
import com.spinyowl.spinygui.core.system.event.SystemScrollEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowCloseEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowFocusEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowIconifyEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowRefreshEvent;
import com.spinyowl.spinygui.core.system.event.SystemWindowSizeEvent;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.time.TimeService;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DefaultSystemEventListenersTest {

  @Test
  void createsExactlyTheStandardWindowListenerSet() {
    SystemEventListenerProviderImpl listeners = composition().listeners();

    assertListener(listeners, SystemCursorPosEvent.class, SystemCursorPosEventListener.class);
    assertListener(listeners, SystemCursorEnterEvent.class, SystemCursorEnterEventListener.class);
    assertListener(listeners, SystemWindowSizeEvent.class, SystemWindowSizeEventListener.class);
    assertListener(listeners, SystemScrollEvent.class, SystemScrollEventListener.class);
    assertListener(listeners, SystemMouseClickEvent.class, SystemMouseClickEventListener.class);
    assertListener(listeners, SystemCharEvent.class, SystemCharEventListener.class);
    assertListener(listeners, SystemKeyEvent.class, SystemKeyEventListener.class);

    assertNull(listeners.listener(SystemCharModsEvent.class));
    assertNull(listeners.listener(SystemFileDropEvent.class));
    assertNull(listeners.listener(SystemFramebufferSizeEvent.class));
    assertNull(listeners.listener(SystemWindowCloseEvent.class));
    assertNull(listeners.listener(SystemWindowFocusEvent.class));
    assertNull(listeners.listener(SystemWindowIconifyEvent.class));
    assertNull(listeners.listener(SystemWindowPosEvent.class));
    assertNull(listeners.listener(SystemWindowRefreshEvent.class));
  }

  @Test
  void preservesIdentitySensitiveDependenciesAcrossListeners() {
    Composition composition = composition();
    SystemEventListenerProviderImpl listeners = composition.listeners();
    Object cursor = listeners.listener(SystemCursorPosEvent.class);
    Object cursorEnter = listeners.listener(SystemCursorEnterEvent.class);
    Object scroll = listeners.listener(SystemScrollEvent.class);
    Object click = listeners.listener(SystemMouseClickEvent.class);
    Object character = listeners.listener(SystemCharEvent.class);
    Object key = listeners.listener(SystemKeyEvent.class);

    assertSame(composition.mouseService(), field(cursor, "mouseService"));
    assertSame(composition.mouseService(), field(cursorEnter, "mouseService"));
    assertSame(composition.mouseService(), field(scroll, "mouseService"));
    assertSame(composition.mouseService(), field(click, "mouseService"));
    assertSame(field(cursor, "scrollbarInteraction"), field(click, "scrollbarInteraction"));
    assertSame(
        field(cursor, "controlTextLayoutService"), field(scroll, "controlTextLayoutService"));
    assertSame(
        field(cursor, "controlTextLayoutService"), field(click, "controlTextLayoutService"));
    assertSame(
        field(cursor, "controlTextLayoutService"), field(character, "controlTextLayoutService"));
    assertSame(field(cursor, "controlTextLayoutService"), field(key, "controlTextLayoutService"));

    Object[] standardListeners = {
      cursor,
      cursorEnter,
      scroll,
      click,
      character,
      key,
      listeners.listener(SystemWindowSizeEvent.class)
    };
    for (Object listener : standardListeners) {
      assertSame(composition.guiEvents(), field(listener, "eventProcessor"));
      assertSame(composition.timeService(), field(listener, "timeService"));
    }
    assertSame(composition.clipboard(), field(key, "clipboard"));
  }

  private Composition composition() {
    DefaultEventProcessor guiEvents = new DefaultEventProcessor();
    TimeService timeService = () -> 1d;
    MouseService mouseService = new MouseServiceImpl();
    Clipboard clipboard = mock(Clipboard.class);
    KeyboardLayout keyboardLayout = new KeyboardLayoutImpl(Map.of());
    TextMeasurer textMeasurer = mock(TextMeasurer.class);
    SystemEventListenerProviderImpl listeners =
        DefaultSystemEventListeners.create(
            guiEvents, timeService, mouseService, clipboard, keyboardLayout, textMeasurer);
    return new Composition(listeners, guiEvents, timeService, mouseService, clipboard);
  }

  private <E extends com.spinyowl.spinygui.core.system.event.SystemEvent> void assertListener(
      SystemEventListenerProviderImpl listeners,
      Class<E> eventType,
      Class<? extends SystemEventListener<?>> listenerType) {
    Object listener = listeners.listener(eventType);
    assertInstanceOf(listenerType, listener);
    assertSame(listener, listeners.listener(eventType));
  }

  private Object field(Object target, String name) {
    for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
      try {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
      } catch (NoSuchFieldException ignored) {
        // Continue through the listener base class.
      } catch (IllegalAccessException failure) {
        throw new AssertionError(failure);
      }
    }
    throw new AssertionError("Missing field " + name + " on " + target.getClass().getName());
  }

  private record Composition(
      SystemEventListenerProviderImpl listeners,
      DefaultEventProcessor guiEvents,
      TimeService timeService,
      MouseService mouseService,
      Clipboard clipboard) {}
}
