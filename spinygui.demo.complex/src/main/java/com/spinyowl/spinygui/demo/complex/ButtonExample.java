package com.spinyowl.spinygui.demo.complex;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.node.ButtonElement;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonExample extends Demo {

  private static final Logger LOG = LoggerFactory.getLogger(ButtonExample.class);
  private static final String XML_RESOURCE = "com/spinyowl/spinygui/demo/button-demo.xml";
  private static final String CSS_RESOURCE = "com/spinyowl/spinygui/demo/button-demo.css";

  private int activationCount;

  public ButtonExample() {
    super(560, 320, "Button Example", new NvgRenderer());
  }

  public static void main(String[] args) {
    Demo demo = new ButtonExample();
    demo.run();
  }

  @Override
  protected Frame createGuiElements(int width, int height) {
    String xml = readResource(XML_RESOURCE);
    String styles = readResource(CSS_RESOURCE);
    Frame frame = nodeParser.fromHtml(xml).frame();
    frame.styleSheets().add(styleSheetParser.parse(styles));

    Text statusText = firstText(frame.getElementById("status"));
    addActivationFeedback(frame, "save", "Save", statusText);
    addActivationFeedback(frame, "nested", "Nested", statusText);
    addActivationFeedback(frame, "input-button", "Input button", statusText);

    return frame;
  }

  private void addActivationFeedback(Frame frame, String id, String label, Text statusText) {
    Element element = frame.getElementById(id);
    if (!isActionButton(element)) {
      throw new IllegalStateException("Button demo element is not actionable: " + id);
    }

    element.addListener(
        ActionEvent.class,
        event -> {
          activationCount++;
          String message = "Activated " + label + " " + activationCount;
          statusText.content(message);
          LOG.info(message);
        });
  }

  private boolean isActionButton(Element element) {
    return element instanceof ButtonElement
        || element instanceof InputElement inputElement && inputElement.buttonInput();
  }

  private Text firstText(Element element) {
    if (element == null
        || element.childNodes().isEmpty()
        || !(element.childNodes().getFirst() instanceof Text text)) {
      throw new IllegalStateException("Button demo status text is missing");
    }
    return text;
  }

  private String readResource(String path) {
    String resource = IOUtil.resourceAsString(path);
    if (resource == null) {
      throw new IllegalStateException("Demo resource not found: " + path);
    }
    return resource;
  }
}
