package com.spinyowl.spinygui.demo.complex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_COLUMN_START;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_ROW_START;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE_AREAS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE_COLUMNS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.GRID_TEMPLATE_ROWS;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.util.IOUtil;

public class GridStyleExample extends Demo {

  private static final String XML_RESOURCE = "com/spinyowl/spinygui/demo/grid-style-demo.xml";
  private static final String CSS_RESOURCE = "com/spinyowl/spinygui/demo/grid-style-demo.css";

  public GridStyleExample() {
    super(720, 460, "Grid Style Example", new NvgRenderer());
  }

  public static void main(String[] args) {
    Demo demo = new GridStyleExample();
    demo.run();
  }

  @Override
  protected Frame createGuiElements(int width, int height) {
    String xml = readResource(XML_RESOURCE);
    String styles = readResource(CSS_RESOURCE);
    Frame frame = nodeParser.fromHtml(xml).frame();
    frame.styleSheets().add(styleSheetParser.parse(styles));

    validateGridStyleContract(xml, styles);
    return frame;
  }

  private void validateGridStyleContract(String xml, String styles) {
    PropertyStoreProvider provider = new DefaultPropertyStoreProvider();
    PropertyStore propertyStore = provider.createPropertyStore();
    StyleSheetParser parser = StyleSheetParserFactory.createParser(propertyStore);
    StyleManager styleManager = new StyleManagerImpl(propertyStore, parser);

    Frame frame = nodeParser.fromHtml(xml).frame();
    frame.styleSheets().add(parser.parse(styles));
    styleManager.recalculate(frame);

    Element grid = requireElement(frame, "visual-stage");
    Element featured = requireElement(frame, "featured-card");

    require(
        Display.GRID.equals(grid.resolvedStyle().display()),
        "grid-probe did not resolve display:grid");
    require(grid.resolvedStyle().getSafe(GRID_TEMPLATE_COLUMNS) != null, "grid columns were not parsed");
    require(grid.resolvedStyle().getSafe(GRID_TEMPLATE_ROWS) != null, "grid rows were not parsed");
    require(grid.resolvedStyle().getSafe(GRID_TEMPLATE_AREAS) != null, "grid areas were not parsed");
    require(featured.resolvedStyle().getSafe(GRID_ROW_START) != null, "grid-row shorthand was not parsed");
    require(featured.resolvedStyle().getSafe(GRID_COLUMN_START) != null, "grid-column shorthand was not parsed");
  }

  private static Element requireElement(Frame frame, String id) {
    Element element = frame.getElementById(id);
    if (element == null) {
      throw new IllegalStateException("Demo element not found: " + id);
    }
    return element;
  }

  private static void require(boolean condition, String message) {
    if (!condition) {
      throw new IllegalStateException(message);
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
