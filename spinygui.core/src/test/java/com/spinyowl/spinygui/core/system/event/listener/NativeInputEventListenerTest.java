package com.spinyowl.spinygui.core.system.event.listener;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_CHECKBOX;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_COLOR;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_PASSWORD;
import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_RANGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.system.event.SystemCharEvent;
import com.spinyowl.spinygui.core.time.TimeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NativeInputEventListenerTest {

  @Mock private EventProcessor eventProcessor;
  @Mock private TimeService timeService;

  @Test
  void charInputDoesNotMutateNonTextNativeOrUnsupportedInputs() {
    assertCharDoesNotEdit(TYPE_CHECKBOX);
    assertCharDoesNotEdit(TYPE_RANGE);
    assertCharDoesNotEdit(TYPE_COLOR);
  }

  @Test
  void charInputEditsPasswordThroughTextFamilyBehavior() {
    InputElement password = focusedInput(TYPE_PASSWORD, "a");
    password.caretIndex(1);
    Frame frame = frame(password);

    listener().process(charEvent(frame, 'b'), frame);

    assertEquals("ab", password.value());
    assertEquals(2, password.caretIndex());
  }

  private void assertCharDoesNotEdit(String type) {
    InputElement input = focusedInput(type, "seed");
    input.caretIndex(input.value().length());
    Frame frame = frame(input);

    listener().process(charEvent(frame, 'x'), frame);

    assertEquals("seed", input.value());
    assertEquals(4, input.caretIndex());
  }

  private SystemCharEventListener listener() {
    return SystemCharEventListener.builder()
        .eventProcessor(eventProcessor)
        .timeService(timeService)
        .build();
  }

  private Frame frame(InputElement input) {
    Frame frame = new Frame();
    frame.addChild(input);
    return frame;
  }

  private InputElement focusedInput(String type, String value) {
    InputElement input = new InputElement();
    input.type(type);
    input.value(value);
    input.focused(true);
    return input;
  }

  private SystemCharEvent charEvent(Frame frame, int codepoint) {
    return SystemCharEvent.builder().frame(frame).codepoint(codepoint).build();
  }
}
