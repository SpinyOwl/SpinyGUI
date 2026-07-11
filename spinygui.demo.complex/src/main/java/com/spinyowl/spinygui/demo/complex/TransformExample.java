package com.spinyowl.spinygui.demo.complex;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.util.IOUtil;

/** A compact visual and interaction proof for static CSS transforms. */
public class TransformExample extends Demo {

  static final String XML_RESOURCE = "com/spinyowl/spinygui/demo/transform-demo.xml";
  static final String CSS_RESOURCE = "com/spinyowl/spinygui/demo/transform-demo.css";

  public TransformExample() {
    super(720, 440, "Static Transform Example", new NvgRenderer());
  }

  public static void main(String[] args) {
    new TransformExample().run();
  }

  @Override
  protected Frame createGuiElements(int width, int height) {
    String xml = readResource(XML_RESOURCE);
    String styles = readResource(CSS_RESOURCE);
    Frame frame = nodeParser.fromHtml(xml).frame();
    frame.styleSheets().add(styleSheetParser.parse(styles));
    validateTransformStyleContract(xml, styles);
    return frame;
  }

  static void validateTransformStyleContract(String xml, String styles) {
    PropertyStoreProvider provider = new DefaultPropertyStoreProvider();
    PropertyStore propertyStore = provider.createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);
    Frame frame = new DefaultNodeParser().fromHtml(xml).frame();
    frame.styleSheets().add(parser.parse(styles));
    styleManager.recalculate(frame);

    requireTransform(frame, "transform-translate");
    requireTransform(frame, "transform-scale");
    requireTransform(frame, "transform-rotate");
    requireTransform(frame, "transform-action");
  }

  private static void requireTransform(Frame frame, String id) {
    Element element = frame.getElementById(id);
    if (element == null
        || element.resolvedStyle().transform() == null
        || element.resolvedStyle().transform() instanceof Transform.None) {
      throw new IllegalStateException("Transform demo did not resolve transform for " + id);
    }
  }

  private static String readResource(String path) {
    String resource = IOUtil.resourceAsString(path);
    if (resource == null) {
      throw new IllegalStateException("Demo resource not found: " + path);
    }
    return resource;
  }
}
