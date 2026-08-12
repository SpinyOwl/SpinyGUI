package com.spinyowl.spinygui.core.system.event.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.CursorEnterEvent;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.event.processor.InputProcessingBatch;
import com.spinyowl.spinygui.core.event.processor.InputProcessingClassification;
import com.spinyowl.spinygui.core.event.processor.InputProcessingResult;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.impl.KeyboardLayoutImpl;
import com.spinyowl.spinygui.core.input.impl.MouseServiceImpl;
import com.spinyowl.spinygui.core.input.impl.ShortcutRegistryImpl;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.system.event.SystemCursorPosEvent;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessorImpl;
import com.spinyowl.spinygui.core.system.event.provider.SystemEventListenerProviderImpl;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.Map;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class SystemInputImpactClassificationTest {

  private static final TimeService TIME = () -> 1D;

  @Test
  void sameInertPointerPathIsUnchangedAndPreservesCoordinates() {
    Frame frame = frameWithInertElement();
    MouseServiceImpl mouse = new MouseServiceImpl();
    mouse.setCursorPositions(
        frame, new com.spinyowl.spinygui.core.input.MouseService.CursorPositions(
            new Vector2f(15, 15), new Vector2f(15, 15)));
    DefaultEventProcessor guiEvents = new DefaultEventProcessor();
    SystemCursorPosEventListener listener =
        SystemCursorPosEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(TIME)
            .mouseService(mouse)
            .build();
    InputProcessingBatch batch = new InputProcessingBatch();

    listener.processWithImpact(cursor(frame, 16, 16), frame, batch);

    assertEquals(InputProcessingResult.UNCHANGED, batch.result());
    assertEquals(InputProcessingClassification.PROVEN_UNCHANGED, batch.classification());
    assertEquals(new Vector2f(16, 16), mouse.getCursorPositions(frame).current());
    assertEquals(new Vector2f(15, 15), mouse.getCursorPositions(frame).previous());
    assertEquals(InputProcessingResult.UNCHANGED, guiEvents.processEventsWithResult());
  }

  @Test
  void pointerBoundaryPressedAndListenerCasesRemainFullRefreshRequired() {
    Frame frame = frameWithInertElement();
    Element child = frame.children().get(0);
    MouseServiceImpl mouse = new MouseServiceImpl();
    mouse.setCursorPositions(
        frame, new com.spinyowl.spinygui.core.input.MouseService.CursorPositions(
            new Vector2f(15, 15), new Vector2f(15, 15)));
    DefaultEventProcessor guiEvents = new DefaultEventProcessor();
    SystemCursorPosEventListener listener =
        SystemCursorPosEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(TIME)
            .mouseService(mouse)
            .build();

    InputProcessingBatch warmup = new InputProcessingBatch();
    listener.processWithImpact(cursor(frame, 16, 16), frame, warmup);

    InputProcessingBatch boundary = new InputProcessingBatch();
    listener.processWithImpact(cursor(frame, 80, 80), frame, boundary);
    assertEquals(InputProcessingResult.FULL_REFRESH_REQUIRED, boundary.result());

    mouse.setCursorPositions(
        frame, new com.spinyowl.spinygui.core.input.MouseService.CursorPositions(
            new Vector2f(16, 16), new Vector2f(16, 16)));
    mouse.pressed(com.spinyowl.spinygui.core.input.MouseButton.LEFT, true);
    InputProcessingBatch pressed = new InputProcessingBatch();
    listener.processWithImpact(cursor(frame, 16, 16), frame, pressed);
    assertEquals(InputProcessingResult.FULL_REFRESH_REQUIRED, pressed.result());

    mouse.pressed(com.spinyowl.spinygui.core.input.MouseButton.LEFT, false);
    child.addListener(CursorEnterEvent.class, ignored -> {});
    InputProcessingBatch listenerBearing = new InputProcessingBatch();
    listener.processWithImpact(cursor(frame, 17, 17), frame, listenerBearing);
    assertEquals(InputProcessingResult.FULL_REFRESH_REQUIRED, listenerBearing.result());
  }

  @Test
  void unusedKeyIsUnchangedButShortcutAndEditingAreFullRefreshRequired() {
    KeyboardLayoutImpl layout = new KeyboardLayoutImpl(Map.of(KeyCode.KEY_A, 65));
    ShortcutRegistryImpl shortcuts = new ShortcutRegistryImpl();
    Keyboard keyboard = new Keyboard(layout, shortcuts);
    DefaultEventProcessor guiEvents = new DefaultEventProcessor();
    SystemKeyEventListener listener =
        SystemKeyEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(TIME)
            .keyboard(keyboard)
            .build();
    Frame frame = new Frame();

    InputProcessingBatch unused = new InputProcessingBatch();
    listener.processWithImpact(key(frame, 65, SystemKeyAction.PRESS), frame, unused);
    assertEquals(InputProcessingResult.UNCHANGED, unused.result());

    shortcuts.shortcut(
        "gameplay", new com.spinyowl.spinygui.core.input.Shortcut(KeyCode.KEY_A, java.util.Set.of()));
    InputProcessingBatch shortcut = new InputProcessingBatch();
    listener.processWithImpact(key(frame, 65, SystemKeyAction.PRESS), frame, shortcut);
    assertEquals(InputProcessingResult.FULL_REFRESH_REQUIRED, shortcut.result());
    assertEquals(InputProcessingClassification.UNKNOWN_FALLBACK, shortcut.classification());

    InputElement input = new InputElement();
    input.value("abc");
    input.caretIndex(2);
    input.focused(true);
    Frame editingFrame = new Frame();
    editingFrame.addChild(input);
    InputProcessingBatch editing = new InputProcessingBatch();
    listener.processWithImpact(
        key(editingFrame, 65, SystemKeyAction.PRESS), editingFrame, editing);
    assertEquals(InputProcessingResult.FULL_REFRESH_REQUIRED, editing.result());
  }

  @Test
  void characterInputIsUnchangedWithoutFocusAndFullRefreshWithFocusedEditor() {
    KeyboardLayoutImpl layout = new KeyboardLayoutImpl(Map.of());
    Keyboard keyboard = new Keyboard(layout, new ShortcutRegistryImpl());
    DefaultEventProcessor guiEvents = new DefaultEventProcessor();
    SystemCharEventListener listener =
        SystemCharEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(TIME)
            .build();

    Frame idleFrame = new Frame();
    InputProcessingBatch idle = new InputProcessingBatch();
    listener.processWithImpact(charEvent(idleFrame, 'a'), idleFrame, idle);
    assertEquals(InputProcessingResult.UNCHANGED, idle.result());

    InputElement input = new InputElement();
    input.focused(true);
    Frame editingFrame = new Frame();
    editingFrame.addChild(input);
    InputProcessingBatch editing = new InputProcessingBatch();
    listener.processWithImpact(charEvent(editingFrame, 'a'), editingFrame, editing);
    assertEquals(InputProcessingResult.FULL_REFRESH_REQUIRED, editing.result());
  }

  @Test
  void mixedBatchRetainsFullRefreshWhenOneEventIsUnknown() {
    Frame frame = frameWithInertElement();
    MouseServiceImpl mouse = new MouseServiceImpl();
    mouse.setCursorPositions(
        frame, new com.spinyowl.spinygui.core.input.MouseService.CursorPositions(
            new Vector2f(15, 15), new Vector2f(15, 15)));
    DefaultEventProcessor guiEvents = new DefaultEventProcessor();
    SystemEventListenerProviderImpl provider = new SystemEventListenerProviderImpl();
    provider.listener(
        SystemCursorPosEvent.class,
        SystemCursorPosEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(TIME)
            .mouseService(mouse)
            .build());
    provider.listener(
        SystemKeyEvent.class,
        SystemKeyEventListener.builder()
            .eventProcessor(guiEvents)
            .timeService(TIME)
            .keyboard(
                new Keyboard(
                    new KeyboardLayoutImpl(Map.of(KeyCode.KEY_A, 65)),
                    new ShortcutRegistryImpl()))
            .build());
    SystemEventProcessorImpl systemEvents =
        SystemEventProcessorImpl.builder().eventListenerProvider(provider).build();

    systemEvents.push(cursor(frame, 16, 16));
    systemEvents.push(key(frame, 999, SystemKeyAction.PRESS));

    assertEquals(
        InputProcessingResult.FULL_REFRESH_REQUIRED, systemEvents.processEventsWithResult());
    assertEquals(InputProcessingResult.UNCHANGED, guiEvents.processEventsWithResult());
  }

  private static Frame frameWithInertElement() {
    Frame frame = new Frame();
    frame.box().contentSize(100, 100);
    Element child = new Element("inert");
    child.box().contentPosition(10, 10);
    child.box().contentSize(20, 20);
    frame.addChild(child);
    return frame;
  }

  private static SystemCursorPosEvent cursor(Frame frame, float x, float y) {
    return SystemCursorPosEvent.builder().frame(frame).posX(x).posY(y).build();
  }

  private static SystemKeyEvent key(Frame frame, int keyCode, SystemKeyAction action) {
    return SystemKeyEvent.builder()
        .frame(frame)
        .keyCode(keyCode)
        .scancode(keyCode)
        .action(action)
        .mods(ImmutableSet.of())
        .build();
  }

  private static SystemCharEvent charEvent(Frame frame, int codepoint) {
    return SystemCharEvent.builder().frame(frame).codepoint(codepoint).build();
  }
}
