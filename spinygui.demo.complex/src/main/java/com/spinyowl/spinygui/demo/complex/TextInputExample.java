package com.spinyowl.spinygui.demo.complex;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.util.IOUtil;

public class TextInputExample extends Demo {

  private static final String XML_RESOURCE = "com/spinyowl/spinygui/demo/text-input-demo.xml";
  private static final String CSS_RESOURCE = "com/spinyowl/spinygui/demo/text-input-demo.css";

  public TextInputExample() {
    super(520, 260, "Text Input Example", new NvgRenderer());
  }

  public static void main(String[] args) {
    Demo demo = new TextInputExample();
    demo.run();
  }

  @Override
  protected Frame createGuiElements(int width, int height) {
    String xml = readResource(XML_RESOURCE);
    String styles = readResource(CSS_RESOURCE);
    Frame frame = nodeParser.fromHtml(xml).frame();
    frame.styleSheets().add(styleSheetParser.parse(styles));
    return frame;
  }

  private String readResource(String path) {
    String resource = IOUtil.resourceAsString(path);
    if (resource == null) {
      throw new IllegalStateException("Demo resource not found: " + path);
    }
    return resource;
  }
}
