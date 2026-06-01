package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.input.KeyAction.PRESS;
import static com.spinyowl.spinygui.core.input.KeyAction.RELEASE;
import static com.spinyowl.spinygui.core.input.KeyAction.REPEAT;
import static com.spinyowl.spinygui.core.node.NodeBuilder.div;
import static com.spinyowl.spinygui.core.node.NodeBuilder.frame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import com.google.common.collect.ImmutableSet;
import com.spinyowl.spinygui.core.event.KeyboardEvent;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.input.KeyAction;
import com.spinyowl.spinygui.core.input.KeyCode;
import com.spinyowl.spinygui.core.input.Keyboard;
import com.spinyowl.spinygui.core.input.KeyboardKey;
import com.spinyowl.spinygui.core.input.KeyboardLayout;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.system.event.SystemKeyEvent;
import com.spinyowl.spinygui.core.system.input.SystemKeyAction;
import com.spinyowl.spinygui.core.system.input.SystemKeyMod;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemKeyEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;
  @Mock private Keyboard keyboard;

  private SystemEventListener<SystemKeyEvent> listener;

  @BeforeEach
  void setUp() {
    listener =
        SystemKeyEventListener.builder()
            .eventProcessor(eventProcessor)
            .timeService(timeService)
            .keyboard(keyboard)
            .build();
  }

  @Test
  void process_pressGeneratesKeyboardEvent() {
    test(SystemKeyAction.PRESS, PRESS);
  }

  @Test
  void process_repeatGeneratesKeyboardEvent() {
    test(SystemKeyAction.REPEAT, REPEAT);
  }

  @Test
  void process_releaseGeneratesKeyboardEvent() {
    test(SystemKeyAction.RELEASE, RELEASE);
  }

  @Test
  void process_doNothingIfNoFocusedElement() {
    // Arrange
    var frame = frame(div());
    SystemKeyEvent systemEvent =
        SystemKeyEvent.builder()
            .keyCode(7)
            .scancode(7)
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .frame(frame)
            .build();

    // Act
    listener.process(systemEvent, frame);

    // Verify
    verifyNoInteractions(eventProcessor);
    verifyNoInteractions(timeService);
    verifyNoInteractions(keyboard);
  }

  @Test
  void process_whenFocusedTextInputHandlesBackspaceAndGeneratesKeyboardEvent() {
    InputElement input = focusedInput("abc", 2);

    processInputKey(input, KeyCode.BACKSPACE, SystemKeyAction.PRESS);

    Assertions.assertEquals("ac", input.value());
    Assertions.assertEquals(1, input.caretIndex());
  }

  @Test
  void process_whenFocusedTextInputHandlesDeleteAndGeneratesKeyboardEvent() {
    InputElement input = focusedInput("abc", 1);

    processInputKey(input, KeyCode.DELETE, SystemKeyAction.PRESS);

    Assertions.assertEquals("ac", input.value());
    Assertions.assertEquals(1, input.caretIndex());
  }

  @Test
  void process_whenFocusedTextInputHandlesBackspaceAtCodePointBoundary() {
    InputElement input = focusedInput("a\uD83D\uDE00b", 3);

    processInputKey(input, KeyCode.BACKSPACE, SystemKeyAction.PRESS);

    Assertions.assertEquals("ab", input.value());
    Assertions.assertEquals(1, input.caretIndex());
  }

  @Test
  void process_whenFocusedTextInputHandlesCaretNavigationWithoutChangingValue() {
    InputElement input = focusedInput("abc", 1);

    processInputKey(input, KeyCode.RIGHT, SystemKeyAction.PRESS);
    Assertions.assertEquals("abc", input.value());
    Assertions.assertEquals(2, input.caretIndex());

    processInputKey(input, KeyCode.LEFT, SystemKeyAction.PRESS);
    Assertions.assertEquals("abc", input.value());
    Assertions.assertEquals(1, input.caretIndex());

    processInputKey(input, KeyCode.END, SystemKeyAction.PRESS);
    Assertions.assertEquals("abc", input.value());
    Assertions.assertEquals(3, input.caretIndex());

    processInputKey(input, KeyCode.HOME, SystemKeyAction.PRESS);
    Assertions.assertEquals("abc", input.value());
    Assertions.assertEquals(0, input.caretIndex());
  }

  @Test
  void process_whenShiftArrowPressedExtendsTextInputSelection() {
    InputElement input = focusedInput("abc", 1);

    processInputKey(
        input, KeyCode.RIGHT, SystemKeyAction.PRESS, ImmutableSet.of(SystemKeyMod.SHIFT));

    Assertions.assertEquals(1, input.selectionStart());
    Assertions.assertEquals(2, input.selectionEnd());
    Assertions.assertEquals(2, input.caretIndex());
  }

  @Test
  void process_whenShiftHomeAndEndPressedExtendsTextInputSelection() {
    InputElement input = focusedInput("abcd", 2);

    processInputKey(
        input, KeyCode.HOME, SystemKeyAction.PRESS, ImmutableSet.of(SystemKeyMod.SHIFT));
    Assertions.assertEquals(0, input.selectionStart());
    Assertions.assertEquals(2, input.selectionEnd());
    Assertions.assertEquals(0, input.caretIndex());

    processInputKey(input, KeyCode.END, SystemKeyAction.PRESS, ImmutableSet.of(SystemKeyMod.SHIFT));
    Assertions.assertEquals(2, input.selectionStart());
    Assertions.assertEquals(4, input.selectionEnd());
    Assertions.assertEquals(4, input.caretIndex());
  }

  @Test
  void process_whenBackspacePressedWithSelectionDeletesSelection() {
    InputElement input = focusedInput("abcd", 3);
    input.select(1, 3);

    processInputKey(input, KeyCode.BACKSPACE, SystemKeyAction.PRESS);

    Assertions.assertEquals("ad", input.value());
    Assertions.assertEquals(1, input.caretIndex());
    Assertions.assertFalse(input.hasSelection());
  }

  @Test
  void process_whenKeyIsReleasedDoesNotEditTextInputButGeneratesKeyboardEvent() {
    InputElement input = focusedInput("abc", 2);

    processInputKey(input, KeyCode.BACKSPACE, SystemKeyAction.RELEASE);

    Assertions.assertEquals("abc", input.value());
    Assertions.assertEquals(2, input.caretIndex());
  }

  @Test
  void process_whenFocusedTextareaHandlesEnterAndGeneratesKeyboardEvent() {
    TextareaElement textarea = focusedTextarea("ab", 1);

    processTextareaKey(textarea, KeyCode.ENTER, SystemKeyAction.PRESS);

    Assertions.assertEquals("a\nb", textarea.value());
    Assertions.assertEquals(2, textarea.caretIndex());
  }

  @Test
  void process_whenFocusedTextareaHandlesLineHomeAndEnd() {
    TextareaElement textarea = focusedTextarea("ab\ncde", 5);

    processTextareaKey(textarea, KeyCode.HOME, SystemKeyAction.PRESS);
    Assertions.assertEquals(3, textarea.caretIndex());

    processTextareaKey(textarea, KeyCode.END, SystemKeyAction.PRESS);
    Assertions.assertEquals(6, textarea.caretIndex());
  }

  @Test
  void process_whenFocusedElementIsNotTextInputKeepsCurrentKeyboardEventFlow() {
    test(SystemKeyAction.PRESS, PRESS);
  }

  @Test
  void process_whenNativeKeyIsUnmappedGeneratesUnknownKeyboardEvent() {
    // Arrange
    var frame = frame();
    var element = div();
    frame.addChild(element);
    element.focused(true);

    double timestamp = 1D;
    when(timeService.currentTime()).thenReturn(timestamp);
    KeyboardLayout keyboardLayout = mock(KeyboardLayout.class);
    when(keyboard.layout()).thenReturn(keyboardLayout);

    int keyCode = 999;
    int scancode = 7;
    SystemKeyEvent event =
        SystemKeyEvent.builder()
            .keyCode(keyCode)
            .scancode(scancode)
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .frame(frame)
            .build();

    KeyboardEvent expectedEvent =
        KeyboardEvent.builder()
            .source(frame)
            .target(element)
            .action(PRESS)
            .timestamp(timestamp)
            .mods(ImmutableSet.of())
            .key(new KeyboardKey(KeyCode.UNKNOWN, keyCode, scancode))
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(eventProcessor).push(expectedEvent);
  }

  private void test(SystemKeyAction systemAction, KeyAction action) {
    // Arrange

    var frame = frame();
    var element = div();
    frame.addChild(element);
    double timestamp = 1D;
    when(timeService.currentTime()).thenReturn(timestamp);
    KeyboardLayout keyboardLayout = mock(KeyboardLayout.class);
    when(keyboard.layout()).thenReturn(keyboardLayout);

    int keyCode = 7;
    KeyCode keyCodeObject = KeyCode.KEY_7;
    int scancode = 7;

    SystemKeyEvent event =
        SystemKeyEvent.builder()
            .keyCode(keyCode)
            .scancode(scancode)
            .action(systemAction)
            .mods(ImmutableSet.of())
            .frame(frame)
            .build();

    when(keyboardLayout.keyCode(keyCode)).thenReturn(keyCodeObject);

    element.focused(true);

    KeyboardEvent expectedEvent =
        KeyboardEvent.builder()
            .source(frame)
            .target(element)
            .action(action)
            .timestamp(timestamp)
            .mods(ImmutableSet.of())
            .key(new KeyboardKey(keyCodeObject, keyCode, scancode))
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(keyboard).layout();
    verify(keyboardLayout).keyCode(keyCode);
    verify(timeService).currentTime();
    verify(eventProcessor).push(expectedEvent);
  }

  private InputElement focusedInput(String value, int caretIndex) {
    InputElement input = new InputElement();
    input.value(value);
    input.caretIndex(caretIndex);
    input.focused(true);
    return input;
  }

  private TextareaElement focusedTextarea(String value, int caretIndex) {
    TextareaElement textarea = new TextareaElement(value);
    textarea.caretIndex(caretIndex);
    textarea.focused(true);
    return textarea;
  }

  private void processInputKey(
      InputElement input, KeyCode mappedKeyCode, SystemKeyAction systemAction) {
    processInputKey(input, mappedKeyCode, systemAction, ImmutableSet.of());
  }

  private void processInputKey(
      InputElement input,
      KeyCode mappedKeyCode,
      SystemKeyAction systemAction,
      ImmutableSet<SystemKeyMod> mods) {
    // Arrange
    var frame = frame(input);
    double timestamp = 1D;
    when(timeService.currentTime()).thenReturn(timestamp);
    KeyboardLayout keyboardLayout = mock(KeyboardLayout.class);
    when(keyboard.layout()).thenReturn(keyboardLayout);

    int keyCode = 7;
    int scancode = 7;

    SystemKeyEvent event =
        SystemKeyEvent.builder()
            .keyCode(keyCode)
            .scancode(scancode)
            .action(systemAction)
            .mods(mods)
            .frame(frame)
            .build();

    when(keyboardLayout.keyCode(keyCode)).thenReturn(mappedKeyCode);

    KeyboardEvent expectedEvent =
        KeyboardEvent.builder()
            .source(frame)
            .target(input)
            .action(getAction(systemAction))
            .timestamp(timestamp)
            .mods(event.mappedMods())
            .key(new KeyboardKey(mappedKeyCode, keyCode, scancode))
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    // Act
    listener.process(event, frame);

    // Verify
    verify(eventProcessor).push(expectedEvent);
  }

  private void processTextareaKey(
      TextareaElement textarea, KeyCode mappedKeyCode, SystemKeyAction systemAction) {
    var frame = frame(textarea);
    double timestamp = 1D;
    when(timeService.currentTime()).thenReturn(timestamp);
    KeyboardLayout keyboardLayout = mock(KeyboardLayout.class);
    when(keyboard.layout()).thenReturn(keyboardLayout);

    int keyCode = 7;
    int scancode = 7;

    SystemKeyEvent event =
        SystemKeyEvent.builder()
            .keyCode(keyCode)
            .scancode(scancode)
            .action(systemAction)
            .mods(ImmutableSet.of())
            .frame(frame)
            .build();

    when(keyboardLayout.keyCode(keyCode)).thenReturn(mappedKeyCode);

    KeyboardEvent expectedEvent =
        KeyboardEvent.builder()
            .source(frame)
            .target(textarea)
            .action(getAction(systemAction))
            .timestamp(timestamp)
            .mods(event.mappedMods())
            .key(new KeyboardKey(mappedKeyCode, keyCode, scancode))
            .build();
    doNothing().when(eventProcessor).push(expectedEvent);

    listener.process(event, frame);

    verify(eventProcessor).push(expectedEvent);
  }

  private KeyAction getAction(SystemKeyAction systemAction) {
    return switch (systemAction) {
      case PRESS -> PRESS;
      case RELEASE -> RELEASE;
      case REPEAT -> REPEAT;
    };
  }

  @Test
  void process_throwsNPE_ifFrameIsNull() {
    SystemKeyEvent event =
        SystemKeyEvent.builder()
            .keyCode(1)
            .scancode(1)
            .action(SystemKeyAction.PRESS)
            .mods(ImmutableSet.of())
            .frame(frame())
            .build();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(event, null));
  }

  @Test
  void process_throwsNPE_ifEventIsNull() {
    Frame frame = frame();
    Assertions.assertThrows(NullPointerException.class, () -> listener.process(null, frame));
  }
}
