package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.ElementLayout;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.layout.TextLayout;
import com.spinyowl.spinygui.core.layout.TextMeasurer;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.manager.StyleManager;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyStore;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.HashMap;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class ParsedInlineWhitespaceLayoutTest {

  @Test
  void layout_whenParsedLabelsContainSpaces_preservesMeasuredSpaceAdvances() {
    Frame frame =
        parseFrame(
            """
            <div style="display: block; width: 300px;">
              <div id="horizontal" style="display: block; width: 300px;">Horizontal auto</div>
              <div id="visible" style="display: block; width: 300px;">Visible overflow</div>
              <div id="boundary" style="display: block; width: 300px;">Hello <span style="display: inline;">wide</span> world</div>
            </div>
            """);

    styleManager().recalculate(frame);
    layoutService().layout(frame);

    Text horizontal = text(frame.getElementById("horizontal"));
    assertEquals("Horizontal", horizontal.inlineFragments().get(0).text());
    assertEquals(" ", horizontal.inlineFragments().get(1).text());
    assertEquals("auto", horizontal.inlineFragments().get(2).text());
    assertEquals(110, horizontal.inlineFragments().get(2).x());

    Text visible = text(frame.getElementById("visible"));
    assertEquals("Visible", visible.inlineFragments().get(0).text());
    assertEquals(" ", visible.inlineFragments().get(1).text());
    assertEquals("overflow", visible.inlineFragments().get(2).text());
    assertEquals(80, visible.inlineFragments().get(2).x());
  }

  @Test
  void layout_whenParsedInlineElementHasBoundarySpaces_preservesVisualSpacing() {
    Frame frame =
        parseFrame(
            """
            <div style="display: block; width: 300px;">
              <div id="boundary" style="display: block; width: 300px;">Hello <span style="display: inline;">wide</span> world</div>
            </div>
            """);

    styleManager().recalculate(frame);
    layoutService().layout(frame);

    Element boundary = frame.getElementById("boundary");
    Text left = boundary.childNodes().get(0).asText();
    Text middle = boundary.childNodes().get(1).asElement().childNodes().get(0).asText();
    Text right = boundary.childNodes().get(2).asText();

    assertEquals(" ", left.inlineFragments().get(1).text());
    assertEquals(60, middle.inlineFragments().get(0).x());
    assertEquals(" ", right.inlineFragments().get(0).text());
    assertEquals(100, right.inlineFragments().get(0).x());
    assertEquals("world", right.inlineFragments().get(1).text());
    assertEquals(110, right.inlineFragments().get(1).x());
  }

  private Frame parseFrame(String source) {
    Frame frame = new Frame();
    frame.frameSize(500, 300);
    frame.addChild(new DefaultNodeParser().fromHtml(source));
    return frame;
  }

  private StyleManager styleManager() {
    PropertyStore propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
    return new StyleManagerImpl(propertyStore, StyleSheetParserFactory.createParser(propertyStore));
  }

  private LayoutService layoutService() {
    var layoutMap = new HashMap<Display, ElementLayout>();
    LayoutService layoutService = new LayoutServiceImpl(mock(TextLayout.class), layoutMap);
    var blockLayout =
        new BlockLayout(layoutService, new InlineFormattingContext(new FixedTextMeasurer()));
    layoutMap.put(Display.NONE, new NoneLayout());
    layoutMap.put(Display.BLOCK, blockLayout);
    layoutMap.put(
        Display.FLEX,
        new FlexLayout(
            mock(SystemEventProcessor.class),
            mock(EventProcessor.class),
            mock(TimeService.class),
            blockLayout,
            layoutService));
    return layoutService;
  }

  private Text text(Element element) {
    return element.childNodes().get(0).asText();
  }

  private static class FixedTextMeasurer implements TextMeasurer {

    @Override
    public TextLineMetrics measure(
        @NonNull String text, @NonNull Font font, float fontSize, float lineHeight) {
      FontMetrics fontMetrics =
          new FontMetrics(8, 2, Math.max(0, fontSize * lineHeight - 10), 10, 8);
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(0)
          .endIndex(text.length())
          .charCount(text.length())
          .width(text.length() * 10f)
          .height(fontMetrics.lineHeight())
          .baseline(fontMetrics.baseline())
          .fontMetrics(fontMetrics)
          .build();
    }
  }
}
