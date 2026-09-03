package com.spinyowl.spinygui.demo.complex;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.binding.HandlerRegistry;
import com.spinyowl.spinygui.core.binding.XmlEventBindingLoader;
import com.spinyowl.spinygui.core.binding.XmlEventBindingOptions;
import com.spinyowl.spinygui.core.event.ActionEvent;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Resource-backed button demo using optional named XML action bindings. */
public class ButtonExample extends Demo {

  /** Logger receiving the same observable activation messages shown in the demo status element. */
  private static final Logger LOG = LoggerFactory.getLogger(ButtonExample.class);

  /** Classpath location of the declaratively bound button view. */
  private static final String XML_RESOURCE = "com/spinyowl/spinygui/demo/button-demo.xml";

  /** Classpath location of the button view's stylesheet. */
  private static final String CSS_RESOURCE = "com/spinyowl/spinygui/demo/button-demo.css";

  /** Monotonic activation count owned by this demo instance for its full runtime. */
  private int activationCount;

  /** Creates the button demo with its existing window and NanoVG renderer configuration. */
  public ButtonExample() {
    super(560, 320, "Button Example", new NvgRenderer());
  }

  /** Runs the interactive button demo. */
  public static void main(String[] args) {
    Demo demo = new ButtonExample();
    demo.run();
  }

  /** Loads the XML through the optional binder and installs the demo-owned named handlers. */
  @Override
  protected Frame createGuiElements(int width, int height) {
    String xml = readResource(XML_RESOURCE);
    String styles = readResource(CSS_RESOURCE);
    HandlerRegistry handlers = new HandlerRegistry();
    Frame frame =
        new XmlEventBindingLoader(nodeParser, handlers, XmlEventBindingOptions.defaults())
            .fromHtml(xml)
            .frame();
    frame.styleSheets().add(styleSheetParser.parse(styles));

    Text statusText = firstText(frame.getElementById("status"));
    registerActivationFeedback(handlers, "save", "Save", statusText);
    registerActivationFeedback(handlers, "save-nested", "Nested", statusText);
    registerActivationFeedback(handlers, "save-input", "Input button", statusText);

    return frame;
  }

  /** Registers one named action that updates status text, count, and logging on every activation. */
  private void registerActivationFeedback(
      HandlerRegistry handlers, String handlerName, String label, Text statusText) {
    handlers.register(
        handlerName,
        ActionEvent.class,
        event -> {
          activationCount++;
          String message = "Activated " + label + " " + activationCount;
          statusText.content(message);
          LOG.info(message);
        });
  }

  /** Returns the status element's text node or fails when the resource contract is broken. */
  private Text firstText(Element element) {
    if (element == null
        || element.childNodes().isEmpty()
        || !(element.childNodes().getFirst() instanceof Text text)) {
      throw new IllegalStateException("Button demo status text is missing");
    }
    return text;
  }

  /** Reads one required classpath resource. */
  private String readResource(String path) {
    String resource = IOUtil.resourceAsString(path);
    if (resource == null) {
      throw new IllegalStateException("Demo resource not found: " + path);
    }
    return resource;
  }
}
