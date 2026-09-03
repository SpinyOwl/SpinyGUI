package com.spinyowl.spinygui.demo.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.event.processor.DefaultEventProcessor;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import org.junit.jupiter.api.Test;

class ButtonExampleTest {

  @Test
  void declarativeActionsPreserveAllButtonDemoStateChanges() {
    ButtonExample example = configuredExample();
    Frame frame = example.createGuiElements(560, 320);
    Element save = frame.getElementById("save");
    Element nested = frame.getElementById("nested");
    InputElement input =
        assertInstanceOf(InputElement.class, frame.getElementById("input-button"));
    Text status = statusText(frame);

    assertEquals("save", save.getAttribute("on-action"));
    assertEquals("save-nested", nested.getAttribute("on-action"));
    assertEquals("save-input", input.getAttribute("on-action"));
    assertEquals(1, save.getListeners(ActionEvent.class).size());
    assertEquals(1, nested.getListeners(ActionEvent.class).size());
    assertEquals(1, input.getListeners(ActionEvent.class).size());
    assertTrue(input.buttonInput());
    assertTrue(
        nested.childNodes().stream()
            .anyMatch(node -> node instanceof Element element && "span".equals(element.nodeName())));
    assertEquals(1, frame.styleSheets().size());
    assertEquals("Ready", status.content());

    activate(save);
    assertEquals("Activated Save 1", status.content());
    activate(nested);
    assertEquals("Activated Nested 2", status.content());
    activate(input);
    assertEquals("Activated Input button 3", status.content());
  }

  private static ButtonExample configuredExample() {
    ButtonExample example = new ButtonExample();
    example.nodeParser = new DefaultNodeParser();
    var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    example.styleSheetParser = StyleSheetParserFactory.createParser(propertyStore);
    return example;
  }

  private static Text statusText(Frame frame) {
    Element status = frame.getElementById("status");
    return assertInstanceOf(Text.class, status.childNodes().getFirst());
  }

  private static void activate(Element target) {
    DefaultEventProcessor processor = new DefaultEventProcessor();
    processor.push(ActionEvent.builder().source(target).target(target).timestamp(0).build());
    processor.processEvents();
  }
}
