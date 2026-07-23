package com.spinyowl.spinygui.core.system.font;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import java.util.List;
import org.junit.jupiter.api.Test;

class FontChainResolverTest {

  @Test
  void resolve_prefersCssFamilyOrderOverRegistryInsertionOrder() {
    Font second =
        Font.addFont(
            new Font(
                "T2 Second Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t2-second.ttf"));
    Font first =
        Font.addFont(
            new Font(
                "T2 First Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t2-first.ttf"));

    List<Font> resolved =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 First Family", "T2 Second Family"),
            FontStyle.NORMAL,
            FontWeight.REGULAR,
            FontStretch.NORMAL);

    assertEquals(List.of(first, second), resolved);
  }

  @Test
  void resolve_skipsUnavailableFamiliesWithoutSelectingOtherRegisteredFonts() {
    List<Font> unavailableOnly =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 Unavailable Family"),
            FontStyle.NORMAL,
            FontWeight.REGULAR,
            FontStretch.NORMAL);
    List<Font> resolved =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 Unavailable Family", Font.DEFAULT.fontFamily()),
            FontStyle.NORMAL,
            FontWeight.REGULAR,
            FontStretch.NORMAL);

    assertTrue(unavailableOnly.isEmpty());
    assertEquals(Font.DEFAULT, resolved.getFirst());
  }

  @Test
  void resolve_retainsNamedLatinIconAndEmojiPrimaryFaces() {
    Font icon =
        Font.addFont(
            new Font(
                "T2 Icon Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t2-icon.ttf"));
    Font emoji =
        Font.addFont(
            new Font(
                "T2 Emoji Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t2-emoji.ttf"));

    assertEquals(
        Font.DEFAULT,
        resolve(List.of(Font.DEFAULT.fontFamily(), icon.fontFamily(), emoji.fontFamily()))
            .getFirst());
    assertEquals(icon, resolve(List.of(icon.fontFamily(), emoji.fontFamily())).getFirst());
    assertEquals(emoji, resolve(List.of(emoji.fontFamily(), icon.fontFamily())).getFirst());
  }

  @Test
  void resolve_prefersExactFaceThenDeterministicNearestFaceWithinAFamily() {
    Font.addFont(
        new Font(
            "T2 Matching Family",
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.BOLD,
            "fonts/t2-fallback.ttf"));
    Font exact =
        Font.addFont(
            new Font(
                "T2 Matching Family",
                FontStyle.ITALIC,
                FontStretch.NORMAL,
                FontWeight.BOLD,
                "fonts/t2-exact.ttf"));
    Font.addFont(
        new Font(
            "T2 Nearest Family",
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.REGULAR,
            "fonts/t2-nearest-regular.ttf"));
    Font nearest =
        Font.addFont(
            new Font(
                "T2 Nearest Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.BOLD,
                "fonts/t2-nearest-bold.ttf"));

    List<Font> exactResolved =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 Matching Family"),
            FontStyle.ITALIC,
            FontWeight.BOLD,
            FontStretch.NORMAL);
    List<Font> fallbackResolved =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 Nearest Family"),
            FontStyle.ITALIC,
            FontWeight.BOLD,
            FontStretch.NORMAL);

    assertEquals(exact, exactResolved.getFirst());
    assertEquals(nearest, fallbackResolved.getFirst());
  }

  private List<Font> resolve(List<String> families) {
    return FontChainResolver.DEFAULT.resolve(
        families, FontStyle.NORMAL, FontWeight.REGULAR, FontStretch.NORMAL);
  }
}
