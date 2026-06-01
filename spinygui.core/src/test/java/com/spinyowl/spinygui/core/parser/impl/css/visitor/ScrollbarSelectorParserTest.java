package com.spinyowl.spinygui.core.parser.impl.css.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import com.spinyowl.spinygui.core.style.stylesheet.StyleSheet;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.stylesheet.selector.CombinatorSelector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.Selector;
import com.spinyowl.spinygui.core.style.stylesheet.selector.pseudoelement.ScrollbarSelector;
import com.spinyowl.spinygui.core.style.types.ScrollbarPart;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ScrollbarSelectorParserTest {

  @Test
  void parse_classWebkitScrollbarSelector() {
    ScrollbarSelector selector = scrollbarSelector(".panel::-webkit-scrollbar { color: red; }");

    assertEquals(ScrollbarPart.SCROLLBAR, selector.part());
    assertEquals("::-webkit-scrollbar", selector.toString());
  }

  @Test
  void parse_legacyScrollbarSelectorAlias() {
    ScrollbarSelector selector = scrollbarSelector(".panel::scrollbar { color: red; }");

    assertEquals(ScrollbarPart.SCROLLBAR, selector.part());
    assertEquals("::-webkit-scrollbar", selector.toString());
  }

  @Test
  void parse_universalWebkitScrollbarThumbSelector() {
    ScrollbarSelector selector = scrollbarSelector("*::-webkit-scrollbar-thumb { color: red; }");

    assertEquals(ScrollbarPart.THUMB, selector.part());
    assertEquals("::-webkit-scrollbar-thumb", selector.toString());
  }

  @Test
  void parse_elementWebkitScrollbarTrackSelector() {
    ScrollbarSelector selector = scrollbarSelector("div::-webkit-scrollbar-track { color: red; }");

    assertEquals(ScrollbarPart.TRACK, selector.part());
    assertEquals("::-webkit-scrollbar-track", selector.toString());
  }

  @Test
  void parse_webkitScrollbarParts() {
    assertEquals(
        ScrollbarPart.TRACK_PIECE,
        scrollbarSelector("div::-webkit-scrollbar-track-piece { color: red; }").part());
    assertEquals(
        ScrollbarPart.BUTTON,
        scrollbarSelector("div::-webkit-scrollbar-button { color: red; }").part());
    assertEquals(
        ScrollbarPart.CORNER,
        scrollbarSelector("div::-webkit-scrollbar-corner { color: red; }").part());
    assertEquals(
        ScrollbarPart.RESIZER,
        scrollbarSelector("div::-webkit-scrollbar-resizer { color: red; }").part());
  }

  @Test
  void parse_unsupportedPseudoElementDoesNotCreateSelector() {
    StyleSheet styleSheet = parse("div::-webkit-scrollbar-unsupported { color: red; }");

    assertTrue(styleSheet.rulesets().getFirst().selectors().isEmpty());
  }

  private ScrollbarSelector scrollbarSelector(String css) {
    Selector selector = parse(css).rulesets().getFirst().selectors().getFirst();
    return findScrollbarSelector(selector).orElseThrow();
  }

  private Optional<ScrollbarSelector> findScrollbarSelector(Selector selector) {
    if (selector instanceof ScrollbarSelector scrollbarSelector) {
      return Optional.of(scrollbarSelector);
    }
    if (selector instanceof CombinatorSelector combinatorSelector) {
      return findScrollbarSelector(combinatorSelector.first())
          .or(() -> findScrollbarSelector(combinatorSelector.second()));
    }
    return Optional.empty();
  }

  private StyleSheet parse(String css) {
    StyleSheetParser parser =
        StyleSheetParserFactory.createParser(
            new DefaultPropertyStoreProvider().createPropertyStore());
    return parser.parse(css);
  }
}
